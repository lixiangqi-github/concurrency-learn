package com.homurax.chapter09.search.concurrent;

import com.homurax.chapter09.search.data.Product;
import com.homurax.chapter09.search.data.ProductLoader;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

public class ConcurrentObjectAccumulator implements BiConsumer<List<Product>, Path> {

    private String word;

    public ConcurrentObjectAccumulator(String word) {
        this.word = word;
    }

    @Override
    public void accept(List<Product> list, Path path) {

        Product product = ProductLoader.load(path);

        if (product.getTitle().toLowerCase().contains(word.toLowerCase())) {
            list.add(product);
        }
    }

}
