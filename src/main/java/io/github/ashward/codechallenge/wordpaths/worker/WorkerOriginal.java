package io.github.ashward.codechallenge.wordpaths.worker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @deprecated
 */
public class WorkerOriginal extends AbstractWorker {

  // Working vars
  private Map<String, Integer> shortestPathToWord;
  private Stack<String> currentPath;

  private enum GetPathResult {
    FOUND,
    NOT_FOUND,
    EXHAUSTED
  }

  public WorkerOriginal(Set<String> sameLengthWords, String startWord, String endWord) {
    super(sameLengthWords, startWord, endWord);
  }

  public synchronized List<String> getPath() {
    currentPath = new Stack<>();
    shortestPathToWord = new HashMap<>();

    currentPath.push(startWord);

    int searchDepth = 0;

    GetPathResult lastResult;

    do {
      lastResult = getPathFromWord(startWord, ++searchDepth);
    } while (lastResult == GetPathResult.NOT_FOUND);

    if (lastResult == GetPathResult.FOUND) {
      currentPath.push(endWord);

      return currentPath;
    }

    return null;
  }

  public GetPathResult getPathFromWord(String thisWord, int searchDepth) {
    boolean searchExhausted = true;

    Integer thisSptw = shortestPathToWord.get(thisWord);

    if (thisSptw == null || thisSptw > currentPath.size()) {
      shortestPathToWord.put(thisWord, currentPath.size());
    }

    Set<String> nextWords = getNextWords(thisWord);

    for (String word : nextWords) {
      if (word.equals(endWord)) {
        return GetPathResult.FOUND;
      } else {
        Integer sptw = shortestPathToWord.get(word);
        if (sptw != null && sptw < currentPath.size()) {
          continue;
        }
      }

      if (currentPath.size() < searchDepth) {
        currentPath.push(word);

        GetPathResult subResult = getPathFromWord(word, searchDepth);

        if (subResult == GetPathResult.FOUND) {
          return subResult;
        } else if (subResult == GetPathResult.NOT_FOUND) {
          searchExhausted = false;
        }

        currentPath.pop();
      } else {
        searchExhausted = false;
      }
    }

    return searchExhausted ? GetPathResult.EXHAUSTED : GetPathResult.NOT_FOUND;
  }
}
