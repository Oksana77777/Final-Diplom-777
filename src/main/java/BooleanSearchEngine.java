import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.apache.maven.surefire.shared.io.FileUtils;
import org.apache.maven.surefire.shared.io.filefilter.SuffixFileFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {

    private HashMap<String, List<PageEntry>> wordListPageEntry;
    private HashSet<String> stopSet;


    public BooleanSearchEngine(File pdfsDir) throws IOException {
        wordListPageEntry = new HashMap<>();
        Iterator it = FileUtils.iterateFiles(pdfsDir, new SuffixFileFilter(".pdf"), null);
        while (it.hasNext()) {
            File pdf = new File(((File) it.next()).getName());
            var doc = new PdfDocument(new PdfReader(pdfsDir + "/" + pdf));
            for (int i = 1; i < doc.getNumberOfPages(); i++) {
                var page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }
                int number = i;
                for (String wordKey : freqs.keySet()) {
                    PageEntry pageEntry = (new PageEntry(pdf.getName(), number, freqs.get(wordKey)));
                    if (wordListPageEntry.containsKey(wordKey)) {
                        wordListPageEntry.get(wordKey).add(pageEntry);
                    } else {
                        List<PageEntry> pageEntryList = new ArrayList<>();
                        pageEntryList.add(pageEntry);
                        wordListPageEntry.put(wordKey, pageEntryList);
                    }
                }
            }
        }
    }

    public void loadStopTxtFile(File txtFile) throws FileNotFoundException {
        stopSet = new HashSet<>();
        if (txtFile.exists() && txtFile.canRead()) {
            try (Scanner scanner = new Scanner(new File(String.valueOf(txtFile)))) {
                while (scanner.hasNextLine()) stopSet.add(scanner.nextLine());
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> request = new ArrayList<>();
        List<String> words = new ArrayList<>(List.of(word.toLowerCase().split("\\P{IsAlphabetic}+")));
        words.removeIf(elementWords -> stopSet.contains(elementWords));
        for (String str : words) {
            if (request.isEmpty()) {
                request.addAll(wordListPageEntry.get(str));
            } else {
                for (PageEntry pageEntry : wordListPageEntry.get(str)) {
                    int size = request.stream()
                            .filter(e -> e.compareNamePage(pageEntry))
                            .toArray().length;
                    if (size > 0) {
                        for (PageEntry pageEntryRequest : request) {
                            if (pageEntryRequest.compareNamePage(pageEntry)) {
                                int countNew = pageEntryRequest.getCount() + pageEntry.getCount();
                                request.set(request.indexOf(pageEntryRequest),
                                        new PageEntry(pageEntry, countNew));
                            }
                        }
                    } else {
                        request.add(pageEntry);
                    }
                }

            }

        }

        return request.stream()
                .sorted()
                .collect(Collectors.toList());
    }
}
