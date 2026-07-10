package ru.store.items;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadLocalRandom;

public class DataAggregator {
    public ProductInfo aggregateProductInfo(String productName) {
        CompletableFuture<Double> priceFuture = CompletableFuture
                .supplyAsync(this::fetchPrice)
                .exceptionally(error -> 0.0);
        CompletableFuture<String> descriptionFuture = CompletableFuture
                .supplyAsync(this::fetchDescription)
                .exceptionally(error -> "Нет описания");
        CompletableFuture<Double> ratingFuture = CompletableFuture
                .supplyAsync(this::fetchRating)
                .exceptionally(error -> 0.0);

        return CompletableFuture
                        .allOf(priceFuture, descriptionFuture, ratingFuture)
                        .thenApply(ignored -> new ProductInfo(
                                productName,
                                priceFuture.join(),
                                descriptionFuture.join(),
                                ratingFuture.join()
                        )).join();
    }

    private double fetchPrice() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        }
        if (ThreadLocalRandom.current().nextInt(100) < 20) {
            throw new RuntimeException("Failed to fetch price");
        }

        return 899.99;
    }

    private String fetchDescription() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        }
        if (ThreadLocalRandom.current().nextInt(100) < 20) {
            throw new RuntimeException("Failed to fetch description");
        }

        return "Описание ноутбука";
    }

    private double fetchRating() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        }
        if (ThreadLocalRandom.current().nextInt(100) < 20) {
            throw new RuntimeException("Failed to fetch rating");
        }

        return 4.7;
    }
}
