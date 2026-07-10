package ru.store.items;

public class Main {
    public static void main(String[] args) {
        DataAggregator aggregator = new DataAggregator();
        ProductInfo product = aggregator.aggregateProductInfo("Ноутбук");
        System.out.println(product);
    }
}
