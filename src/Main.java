import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

    private static double calcular(double valor) {

        double resultado = valor;

        for (int i = 0; i < 1000; i++) {

            resultado +=
                    Math.sin(valor + i)
                            * Math.cos(valor - i)
                            * Math.sqrt(Math.abs(valor) + 1);
        }

        return resultado;
    }

    private static double[][] gerarMatriz(int linhas, int colunas) {

        double[][] matriz = new double[linhas][colunas];

        for (int i = 0; i < linhas; i++) {

            for (int j = 0; j < colunas; j++) {

                int valorBase =
                        ((i + 1) * 31 + (j + 1) * 17) % 100;

                matriz[i][j] =
                        (valorBase + 1) / 10000.0;
            }
        }

        return matriz;
    }

    private static double processarSequencial(double[][] matriz) {

        double resultado = 0.0;

        for (int i = 0; i < matriz.length; i++) {

            for (int j = 0; j < matriz[i].length; j++) {

                resultado += calcular(matriz[i][j]);
            }
        }

        return resultado;
    }

    private static double somarFaixa(
            double[][] matriz, int linhaInicio, int linhaFim) {

        double somaParcial = 0.0;

        for (int i = linhaInicio; i < linhaFim; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                somaParcial += calcular(matriz[i][j]);
            }
        }

        return somaParcial;
    }

    private static void shutdownExecutor(ExecutorService executor) {

        executor.shutdown();

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static double processarParaleloNaoEstruturado(
            double[][] matriz, int totalTarefas) {

        int totalLinhas = matriz.length;
        int tarefas = Math.min(totalTarefas, totalLinhas);

        ExecutorService executor =
                Executors.newFixedThreadPool(tarefas);

        List<Future<Double>> futures = new ArrayList<>();

        int linhasPorTarefa =
                (int) Math.ceil((double) totalLinhas / tarefas);

        double resultado;

        try {

            for (int t = 0; t < tarefas; t++) {

                int linhaInicio = t * linhasPorTarefa;
                int linhaFim =
                        Math.min(linhaInicio + linhasPorTarefa, totalLinhas);

                if (linhaInicio >= linhaFim) {
                    continue;
                }

                Future<Double> future =
                        executor.submit(() ->
                                somarFaixa(matriz, linhaInicio, linhaFim));

                futures.add(future);
            }

            resultado = 0.0;

            for (Future<Double> future : futures) {
                resultado += future.get();
            }

        } catch (InterruptedException | ExecutionException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Falha ao processar a matriz em paralelo", e);

        } finally {
            shutdownExecutor(executor);
        }

        return resultado;
    }

    private static void exibirMenu() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("      PROJETO DE COMPUTAÇÃO PARALELA");
        System.out.println("==========================================");
        System.out.println("1 - Matriz 500 x 500");
        System.out.println("2 - Matriz 1000 x 1000");
        System.out.println("3 - Matriz 1500 x 1500");
        System.out.println("4 - Matriz 2000 x 2000");
        System.out.println("0 - Exit");
        System.out.println("==========================================");
        System.out.print("Escolha uma opção: ");
    }

    private static void executarComparacao(
            int linhas, int colunas, int totalTarefas) {

        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println(
                "Matriz: " + linhas + " x " + colunas
                        + " | Tarefas (V2): " + totalTarefas);
        System.out.println("------------------------------------------");

        double[][] matriz = gerarMatriz(linhas, colunas);

        long inicioSeq = System.nanoTime();
        double resultadoSeq = processarSequencial(matriz);
        double tempoSeqMs = (System.nanoTime() - inicioSeq) / 1_000_000.0;

        long inicioPar = System.nanoTime();
        double resultadoPar =
                processarParaleloNaoEstruturado(matriz, totalTarefas);
        double tempoParMs = (System.nanoTime() - inicioPar) / 1_000_000.0;

        double speedup = tempoSeqMs / tempoParMs;

        boolean resultadoCorreto =
                Math.abs(resultadoSeq - resultadoPar) < 1e-6;

        System.out.println();
        System.out.printf(Locale.US, "Resultado sequencial: %.6f%n", resultadoSeq);
        System.out.printf(Locale.US, "Resultado paralelo:   %.6f%n", resultadoPar);
        System.out.println();
        System.out.printf(Locale.US, "Tempo sequencial: %.3f ms%n", tempoSeqMs);
        System.out.printf(Locale.US, "Tempo paralelo:   %.3f ms%n", tempoParMs);
        System.out.printf(Locale.US, "Speedup: %.2fx%n", speedup);
        System.out.println("Resultado correto: " + resultadoCorreto);
        System.out.println("------------------------------------------");
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int opcao;

        do {
            exibirMenu();
            opcao = scanner.nextInt();

            switch (opcao) {

                case 1, 2, 3, 4 -> {

                    int tamanho = switch (opcao) {
                        case 1 -> 500;
                        case 2 -> 1000;
                        case 3 -> 1500;
                        default -> 2000;
                    };

                    System.out.print(
                            "Quantidade de tarefas para a V2 (paralela): ");
                    int totalTarefas = scanner.nextInt();

                    executarComparacao(tamanho, tamanho, totalTarefas);
                }

                case 0 -> {
                    System.out.println();
                    System.out.println("Encerrando o programa...");
                }

                default -> {
                    System.out.println();
                    System.out.println("Opção inválida!");
                }
            }

        } while (opcao != 0);

        scanner.close();

        System.out.println("Programa encerrado.");
    }
}
