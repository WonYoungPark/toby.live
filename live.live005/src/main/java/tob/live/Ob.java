package tob.live;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by khcheon on 2016-11-26.
 */
public class Ob {

    // Iterable <======> Observable
    // Pull     <======> Push

    /*
    Iterable
     */
    static void iterableExam() {
        //    Iterable<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Iterable<Integer> list = () ->
                new Iterator<Integer>() {
            int i = 0;
            final static int MAX = 10;
            @Override
            public boolean hasNext() {
                return i < MAX;
            }

            @Override
            public Integer next() {
                return ++i;
            }
        };

        for (Integer i : list) {
            System.out.println(i);
        }

        for (Iterator<Integer> it = list.iterator() ; it.hasNext();) {
            System.out.println(it.next());
        }
    }


    /*
    http://www.reactive-streams.org/
    GOF Observer pattern, Java Observable has Some Problems..
    1. Complete?? Finish??
    2. Error??

    Big 2!
    http://reactivex.io/

     */
    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 10; i ++) {
                setChanged();
                notifyObservers(i); //Observable.notifyObservers() (Push) VS Iterator.next() (Pull)
            }
        }
    }

    /*
    Observable
     */
    static void observableExam() {
        //Observable //Source -> Event/Data -> Observer
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        Observer ob2 = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + "2 " + arg);
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);
        io.addObserver(ob2);


        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);
        //io.run();

        System.out.println(Thread.currentThread().getName() + " EXIT");
        es.shutdown();
    }


    public static void main(String[] args) {
//        observableExam();
        observableExam2WithThread();

    }

    /*
    Observable
     */
    static void observableExam2WithThread() {
        //Observable //Source -> Event/Data -> Observer
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        Observer ob2 = new ObserverThread();

        IntObservable io = new IntObservable();
        io.addObserver(ob);
        io.addObserver(ob2);


        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);
        //io.run();

        System.out.println(Thread.currentThread().getName() + " EXIT");
        es.shutdown();
    }


    static class ObserverThread implements Observer {

        ExecutorService es = Executors.newSingleThreadExecutor();
        @Override
        public void update(Observable o, Object arg) {
            Runnable r = () -> {
                System.out.println(Thread.currentThread().getName() + "2 " + arg);
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            };
            es.execute(r);
        }
    }
}
