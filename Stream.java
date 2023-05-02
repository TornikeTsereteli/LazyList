package debili;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import java.lang.RuntimeException;



public class Stream<T> {

    // head of the stream
    private T _head;

    // lazy tail of the stream
    private Thunk<Stream<T>> _tail;

    public Stream(T head, Thunk<Stream<T>> tail) {
        this._head = head;
        this._tail = tail;
    }

    // Returns the head of the stream.
    public T headS() {
        return _head;
    }

    // Returns the unevaluated tail of the stream.
    public Thunk<Stream<T>> tailS() {
        return _tail;
    }

    // Returns the evaluated tail of the stream.
    public Stream<T> next() {
        // not yet implemented
        return _tail.get();
    }

    // .------------------------------.
    // | Static constructor functions |
    // '------------------------------'

    // Construct a stream by repeating a value.
    public static <U> Stream<U> repeatS(U x) {
        // not yet implemented
        return new Stream(x,Thunk.delay(()->repeatS(x)));
    }

    // Construct a stream by repeatedly applying a function.
    public static <U> Stream<U> iterateS(Function<U, U> f, U x) {
        // not yet implemented
        return new Stream(x,Thunk.delay(()->iterateS(f,f.apply(x))));
    }

    // Construct a stream by repeating a list forever. (Sadly no pure single linked lists :( )
    public static <U> Stream<U> cycleS(List<U> l) {
        // not yet implemented
        return h(l,new LinkedList<>(l) );
    }

    private static <U> Stream<U> h(List<U> l, List<U> l1){
        if(l.isEmpty())
            return cycleS(l1);
        U a = l.get(0);
        LinkedList<U> j = new LinkedList<>(l.subList(1,l.size()));

        return new Stream<>(a,Thunk.delay(()->h(j,l1)));
    }


    private static <U>Stream<U> cycle(List<U> l,Stream<U> stream){


        for (U i:l){
            stream._head = i;
            stream = stream.tailS().get();
        }

        return cycle(l,stream);

    }

    // Construct a stream by counting numbers starting from a given one.
    public static Stream<Integer> fromS(int x) {
        // not yet implemented
        return new Stream<>(x,Thunk.delay(()->fromS(x+1)));
    }

    // Same as @{fromS} but count with a given step width.
    public static Stream<Integer> fromThenS(int x, int d) {
        // not yet implemented
        return new Stream<>(x,Thunk.delay(()->fromThenS(x+d,d)));
    }

    // .------------------------------------------.
    // | Stream reduction and modification (pure) |
    // '------------------------------------------'

    // Fold a stream from the left.
    public <R> R foldrS(BiFunction<T, Thunk<R>, R> f) {
        // not yet implemented
        return f.apply(_head,Thunk.delay(()->tailS().get().foldrS(f)));
    }

    // Filter stream with a predicate. (Returns a lazy result.)
    public Thunk<Stream<T>> filterS(Predicate<T> p) {
        // not yet implemented
//        Stream<T> t = tailS().get();
        if (p.test(_head)){
            return Thunk.delay(()->new Stream<>(headS(),tailS().get().filterS(p)));
        }
        else {
            return tailS().get().filterS(p);
        }

    }

    // Take a given amount of elements from the stream.
    public LinkedList<T> takeS(int n) {
        // not yet implemented
        LinkedList<T> elements = new LinkedList<>();
        Stream<T> stream = this;
        while (n!=0){
            elements.add(stream._head);
            stream = stream.tailS().get();
            n-=1;
        }

        return elements;
    }

    // Drop a given amount of elements from the stream.
    public Stream<T> dropS(int n) {
        // not yet implemented
        if (n==0)
            return this;
        else
            return tailS().get().dropS(n-1);
    }

    // Combine 2 streams with a function.
    public <U, R> Stream<R> zipWithS(BiFunction<T, U, R> f, Stream<U> other) {
        // not yet implemented
        return new Stream<>(f.apply(headS(),other._head),Thunk.delay(()->tailS().get().zipWithS(f,other.tailS().get())));
    }

    // Map every value of the stream with a function, returning a new stream.
    public <R> Stream<R> fmap(Function<T, R> f) {
        // not yet implemented
        return new Stream<>(f.apply(_head),Thunk.delay(()->tailS().get().fmap(f)));
    }

    // Helper class, to create cyclic declarations, may be helpful for generating the fibonacci numbers.
    public static class CyclicRef<T> {
        public T value;
    }

    // Return the stream of all fibonacci numbers.
    public static Stream<Integer> fibS() {
        // Hint: Use CyclicRef to refer to the not yet computed sequenceist
        // not yet implemented
        return new Stream<>(0,Thunk.delay(()->new Stream<>(1,Thunk.delay(()->helper(0,1)))));
    }

    private static Stream<Integer>helper(int i, int j){
        return new Stream<>(i+j,Thunk.delay(()->helper(j,i+j)));
    }

    // Return the stream of all prime numbers.
    public static Stream<Integer> primeS() {

        // not yet implemented
        return h2(2,new LinkedList<>());
    }

    private static Stream<Integer> h2(int n,List<Integer> primes){
        for (int i:primes){
            if (n%i==0)
                return h2(n+1,primes);
        }
        primes.add(n);
        return new Stream<>(n,Thunk.delay(()->h2(n+1,primes)));
    }

    public static void main(String[] args) {
        Thunk<Integer> integerThunk = Thunk.now(5);

        Function<Integer,Integer> f = (x)->3*x+1 ;
        Stream<Integer> stream = Stream.iterateS(f,1);

        Stream<Integer> s = Stream.cycleS(List.of(1,2,3));
        System.out.println(s.takeS(10));


        Stream<Integer> stream2 = stream.filterS(x->x%2==0).get();
        System.out.println(stream2.takeS(10));
        System.out.println(stream.takeS(10));

        System.out.println(s.takeS(10));

        System.out.println("__________________________TESTS_________________________");

        System.out.println("FromS");
        Stream<Integer> s1 = Stream.fromS(1);
        System.out.println(s1.takeS(10));

        System.out.println("Froms_d");
        Stream<Integer> s2 = Stream.fromThenS(3,5);
        System.out.println(s2.takeS(10));

        System.out.println("repeatS");
        Stream<Integer> s3 = Stream.repeatS(66);
        System.out.println(s3.takeS(10));

        System.out.println("iterateS: f = 3x-1, starts from x = 1");
        Stream<Integer> s4 = Stream.iterateS((x)->3*x-1,1);
        System.out.println(s4.takeS(10));

        System.out.println("Cycles, l = [1,2,3,4,5] ");
        Stream<Integer> s5 = Stream.cycleS(List.of(1,2,3,4,5));
        System.out.println(s5.takeS(20));

        System.out.println("filter 3x-1 stream, with predicate x%2==0");
        System.out.println(s4.filterS(x->x%2==0).get().takeS(10));

        System.out.println("drops 5 element of function 2x");
        Stream<Integer> s6 = Stream.iterateS(x->2*x,1);
        System.out.println(s6.dropS(10).takeS(10));

        System.out.println();

        System.out.println("Zipwith 2x stream + 3x");
        Stream<Integer> s7 = Stream.iterateS(x->2*x,1);
        Stream<Integer> s8 = Stream.iterateS(x->3*x,1);

        System.out.print ("2x->  ");
        System.out.println(s7.takeS(10));


        System.out.print ("3x->  ");
        System.out.println(s8.takeS(10));

        System.out.println(s7.zipWithS(Integer::sum,s8).takeS(10));


        System.out.println("Fibonacci");
        System.out.println(fibS().takeS(10));

        System.out.println("Prime Numbers stream");
        Stream<Integer> s9 = Stream.primeS();
        System.out.println(s9.takeS(100));


        System.out.println("1 2 4 8 16 32 64 128 256 -> 0 1 3  7. ......");
        System.out.println(Stream.iterateS(x->x*2,1).fmap(x->x-1).takeS(10));



    }
}
