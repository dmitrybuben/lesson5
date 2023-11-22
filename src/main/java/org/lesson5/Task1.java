package org.lesson5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task1 {

    private static final int CHAR_BOUND_L = 65; // номер начального символа латинского алфавита
    private static final int CHAR_BOUND_H = 90; // номер начального символа латинского алфавита
    private static final String TO_SEARCH = "GeekBrains";
    private static Random random = new Random();

    /**
     * 1.  Создать 2 текстовых файла, примерно по 50-100 символов в каждом(особого значения не имеет);
     * 2.  Написать метод, «склеивающий» эти файлы, то есть вначале идет текст из первого файла, потом текст из второго.
     * 3.* Написать метод, который проверяет, присутствует ли указанное пользователем слово в файле (работаем только с латиницей).
     * 4.* Написать метод, проверяющий, есть ли указанное слово в папке
     */
    public static void main(String[] args) {
        //System.out.println(generateSymbols(50));
        writeFileContents("sample01.txt", 90, 1);
        writeFileContents("sample02.txt", 5);
        concatenate("sample01.txt", "sample02.txt", "sampleOut.txt");
        System.out.println(searchInFile("sampleOut.txt", TO_SEARCH));

        // создать 10 файлов для поиска
        String[] fileNames = new String[10];
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = "file_" + i + ".txt";
            writeFileContents(fileNames[i], 90, 2);
            System.out.printf("Файл создан.\n", fileNames[i]);
        }
        // Поиск в текущей директории
        try {
            List<String> listResult = searchMatch(new File("."), TO_SEARCH);
            for (String s : listResult) {
                System.out.printf("Файл %s содержит искомое слово '%s'\n", s, TO_SEARCH);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Копия текущей директории
        try {
            copyDirectory(new File("."), "./backup");

        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        }

        // Копия с поддиректориями
        try {
            copyAll(new File("."), Path.of("./backupAll"));
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        }
    }

    /**
     * Метод генерации последовательности символов
     *
     * @param amount кол-во символов
     * @return
     */
    private static String generateSymbols(int amount) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < amount; i++) {
            stringBuffer.append((char) random.nextInt(CHAR_BOUND_L, CHAR_BOUND_H + 1));
        }
        return stringBuffer.toString();
    }

    /**
     * Записать последовательность символов в файл
     *
     * @param fileName
     * @param lenght   количество символов
     */
    private static void writeFileContents(String fileName, int lenght) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            fileOutputStream.write(generateSymbols(lenght).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Записать последовательность символов в файл с добавлением искомой фразы с вероятностью 33%
     *
     * @param fileName
     * @param lenght   кол-во символов
     * @param words    кол-во слов(последовательностей) для записи
     */
    private static void writeFileContents(String fileName, int lenght, int words) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            for (int i = 0; i < words; i++) {
                if (random.nextInt(2) == 0) // 33%
                {
                    fileOutputStream.write(TO_SEARCH.getBytes());
                } else {
                    fileOutputStream.write(generateSymbols(lenght).getBytes());
                }
            }
            fileOutputStream.write(generateSymbols(lenght).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Склеить содержимое 2 файлов в 1
     *
     * @param fileName1
     * @param fileName2
     * @param fileOut
     */
    private static void concatenate(String fileName1, String fileName2, String fileOut) {
        // На запись
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {

            int c;
            // На чтение 1
            try (FileInputStream fileInputStream = new FileInputStream(fileName1)) {
                while ((c = fileInputStream.read()) != -1)
                    fileOutputStream.write(c);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // На чтение 2
            try (FileInputStream fileInputStream = new FileInputStream(fileName2)) {
                while ((c = fileInputStream.read()) != -1)
                    fileOutputStream.write(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean searchInFile(String fileName, String search) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            byte[] searchData = search.getBytes(); // перевели в массив байтов
            int c;
            int counter = 0;
            while ((c = fileInputStream.read()) != -1) {
                if (c == searchData[counter]) {
                    counter++;
                } else {
                    counter = 0;
                    if (c == searchData[counter]) {
                        counter++;
                        continue;
                    }
                }
                if (counter == searchData.length) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //поиск в директории
    static List<String> searchMatch(File dir, String search) throws IOException {
        List<String> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null) {
            return list;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                continue;
            if (searchInFile(files[i].getCanonicalPath(), search)) {
                list.add(files[i].getName());
            }
        }
        return list;
    }

    // HW5: Написать функцию, создающую резервную копию всех файлов в директории во вновь созданную папку ./backup
    // избегая директории
    private static void copyDirectory(File dir, String backUpDir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            Files.createDirectory(Path.of(backUpDir));
            for (File file : files) {
                if (file.isDirectory()) {
                    continue;
                }
                Path source = file.toPath();
                Path destination = Path.of(backUpDir, file.getName());
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     * Создать копию файлов и поддиректорий
     * @param source
     * @param target
     * @throws IOException
     */
    private static void copyAll(File source, Path target) throws IOException {
        if (source.isDirectory()) {
            File[] files = source.listFiles();
            Files.createDirectories(target);
            if (files != null) {
                for (File file : files) {
                    copyAll(file, target.resolve(file.getName()));
                }
            }
        } else {
            Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}