package kz.lab.task2;

import kz.lab.annotations.Cached;

public interface Base {
    String method();

    @Cached
    String methodCached();

    void setValue(String data);


}
