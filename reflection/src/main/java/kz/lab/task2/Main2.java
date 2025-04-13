package kz.lab.task2;

public class Main2 {
    public static void main(String[] args) {
        Base child = new Child();

        Base cached = CachingProxyV2.cache(child);

        System.out.println(cached.method()); // Child compute called
        System.out.println(cached.method()); // не из кэша; прямой вызов тк нет аннотации Cached
        cached.setValue("new data");
        System.out.println(cached.method()); // не из кэша; прямой вызов тк нет аннотации Cached
        System.out.println(cached.method()); // не из кэша; прямой вызов тк нет аннотации Cached
        System.out.println(cached.methodCached()); // put into cache
        System.out.println(cached.methodCached()); //  == get from cache ==

        Base parent = new Parent();
        Base cachedParent = CachingProxyV2.cache(parent);

        System.out.println(cachedParent.method()); // Child compute called
        System.out.println(cachedParent.method()); // из кэша; тк Cached exists
        cachedParent.setValue("new data");
        System.out.println(cachedParent.method()); // не из кэша; прямой вызов тк нет аннотации Cached
        System.out.println(cachedParent.method()); // не из кэша; прямой вызов тк нет аннотации Cached
    }
}
