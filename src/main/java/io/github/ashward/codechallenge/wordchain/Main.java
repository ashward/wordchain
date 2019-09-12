package io.github.ashward.codechallenge.wordchain;

import io.github.ashward.codechallenge.wordchain.worker.AbstractWorker;
import io.github.ashward.codechallenge.wordchain.worker.WorkerBidirectional;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
  private Map<Integer, Set<String>> words = new HashMap<>();

  // Set up an executor for some parallelism
  ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  public static void main(String[] args) throws Exception {
    Main main = new Main();

    File inputFile = new File(args[0]);

    boolean showTiming = (args.length > 1 && args[1].equals("-t"));

    main.processFile(inputFile, showTiming);
  }

  public Main() throws Exception {
    loadWordsList();
  }

  public void processFile(File inputFile, boolean showTiming) throws Exception {
    try (Scanner scanner = new Scanner(inputFile)) {
      List<Future<List<String>>> results = new ArrayList<>();

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        String[] words = line.split("\\s+");

        if (words.length != 2) {
          continue;
        }

        if (showTiming) {
          results.add(executor.submit(() -> {
            long time = System.currentTimeMillis();

            try {
              return getWordPath(words[0], words[1]);
            } finally {
              System.out.println(words[0] + " -> " + words[1] + " = " + (System.currentTimeMillis() - time) + "ms");
            }
          }));
        } else {
          results.add(executor.submit(() -> getWordPath(words[0], words[1])));
        }
      }

      // Go through the futures and print out the results (in order) as they appear
      for (Future<List<String>> result : results) {
        if (result.get() != null) {
          System.out.println(result.get().size() + " " + String.join(",", result.get()));
        }
      }

//      results.stream()
//              .map(this::futureGetOrNull)
//              .filter(result -> result != null)
//              .forEach(result -> System.out.println(result.size() + " " + String.join(",", result)));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // re-set the interrupted flag
    } finally {
      executor.shutdownNow();
    }
  }

  /**
   * A {@link RuntimeException} to wrap an {@link InterruptedException} so that we can break execution if required
   */
  private class RuntimeInterruptedException extends RuntimeException {
    public RuntimeInterruptedException(InterruptedException cause) {
      super(cause);
    }
  }

  private <T> T futureGetOrNull(Future<T> future) {
    try {
      return future.get();
    } catch (InterruptedException e) {
      throw new RuntimeInterruptedException(e);
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    return null;
  }

  private void loadWordsList() throws Exception {
    try (ObjectInputStream ois = new ObjectInputStream(this.getClass().getResourceAsStream("/50kwords.obj"))) {
      words = (Map<Integer, Set<String>>) ois.readObject();

      return;
    } catch (Exception e) {
      System.err.println("Error while loading serialised words. Reading from text file");
    }

    InputStream wordsFile = this.getClass().getResourceAsStream("/50kwords.txt");

    try (Scanner scanner = new Scanner(wordsFile)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (!words.containsKey(line.length())) {
          words.put(line.length(), new HashSet<>());
        }

        words.get(line.length()).add(line);
      }
    }
  }

  public List<String> getWordPath(String startWord, String endWord) {
    AbstractWorker worker = new WorkerBidirectional(words.get(startWord.length()), startWord, endWord);

    return worker.getPath();
  }

}
