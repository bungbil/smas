package billy.webui.printer;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class PrintJobWatcher {
  boolean done = false;

  public PrintJobWatcher(DocPrintJob job) {
    job.addPrintJobListener(new PrintJobAdapter() {
      void allDone() {
        synchronized (PrintJobWatcher.this) {
          done = true;
          System.out.println("Printing done ...");
          PrintJobWatcher.this.notify();
        }
      }

      @Override
      public void printJobCanceled(PrintJobEvent pje) {
        allDone();
      }

      @Override
      public void printJobCompleted(PrintJobEvent pje) {
        allDone();
      }

      @Override
      public void printJobFailed(PrintJobEvent pje) {
        allDone();
      }

      @Override
      public void printJobNoMoreEvents(PrintJobEvent pje) {
        allDone();
      }
    });
  }

  public synchronized void waitForDone() {
    try {
      while (!done) {
        wait();
      }
    } catch (InterruptedException e) {
    }
  }
}
