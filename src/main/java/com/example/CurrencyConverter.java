package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CurrencyConverter {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/94e14795b3b8bca7c0159af9/latest/";

    public static void main(String[] args) {
        int choice = 0;

        displayWelcomeMessage();

        Scanner scanner = new Scanner(System.in);
        while (choice != 7) {
            displayMenu();
            choice = scanner.nextInt();

            if (choice == 7) {
                System.out.println("Saliendo. Gracias por usar la app");
                break;
            }

            System.out.println("Ingrese el monto: ");
            double amount = scanner.nextDouble();

            try {
                handleConversionChoice(choice, amount);
            } catch (IOException | InterruptedException e) {
                System.out.println("Error en la conversion: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void displayWelcomeMessage() {
        System.out.println("--------------------------------------------------");
        System.out.println("Conversor de Monedas");
    }

    private static void displayMenu() {
        String menu = """
                1) USD a CLP
                2) CLP a USD
                3) USD a ARS
                4) ARS a USD
                5) USD a BRL
                6) BRL a USD
                7) SALIR
                """;
        System.out.println(menu);
        System.out.println("--------------------------------------------------");
        System.out.println("Seleccione una opción del 1-7:");
    }

    private static void handleConversionChoice(int choice, double amount) throws IOException, InterruptedException {
        String fromCurrency = "";
        String toCurrency = "";

        switch (choice) {
            case 1:
                fromCurrency = "USD";
                toCurrency = "CLP";
                break;
            case 2:
                fromCurrency = "CLP";
                toCurrency = "USD";
                break;
            case 3:
                fromCurrency = "USD";
                toCurrency = "ARS";
                break;
            case 4:
                fromCurrency = "ARS";
                toCurrency = "USD";
                break;
            case 5:
                fromCurrency = "USD";
                toCurrency = "BRL";
                break;
            case 6:
                fromCurrency = "BRL";
                toCurrency = "USD";
                break;
            default:
                System.out.println("Opción no válida");
                return;
        }

        performConversion(fromCurrency, toCurrency, amount);
    }

    private static void performConversion(String fromCurrency, String toCurrency, double amount) throws IOException, InterruptedException {
        String url = API_URL + fromCurrency;

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        double exchangeRate = parseExchangeRate(httpResponse.body(), toCurrency);

        double convertedAmount = amount * exchangeRate;

        System.out.println("La cantidad " + amount + " (" + fromCurrency + ") equivale a " + convertedAmount + " (" + toCurrency + ")\n");
    }

    private static double parseExchangeRate(String jsonResponse, String toCurrency) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");
        return rates.get(toCurrency).getAsDouble();
    }
}
