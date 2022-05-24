package io.github.emanuelvictor.csv;

import io.github.emanuelvictor.commons.reflection.Reflection;
import io.github.emanuelvictor.commons.typing.TypeResolver;
import io.github.emanuelvictor.csv.aspect.Label;
import io.github.emanuelvictor.csv.aspect.Index;
import io.github.emanuelvictor.csv.domain.Field;
import io.github.emanuelvictor.csv.exception.IndexCannotBeLessThan1Exception;
import io.github.emanuelvictor.csv.exception.RepeatedIndexException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.emanuelvictor.commons.normalizer.Normalizer.normalize;
import static org.apache.logging.log4j.util.Strings.join;

public abstract class ReportProcessor<T> implements IReportProcessor<T> {


    /**
     *
     */
    private final Class<T> clazz = TypeResolver.getTypeArguments(ReportProcessor.class, this);

    /**
     *
     */
    private static final Character DEFAULT_SEPARATOR = ';';

    /**
     *
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     *
     */
    private String tempOutput = "target/tmp";

    /**
     * @return List
     */
    public List<Field> extractFields() {

        final List<String> fieldsInString = Reflection.getFields(this.clazz);
        final Field[] fieldsWithPositionAnnotation = new Field[fieldsInString.size()];
        final List<Field> fieldsWithoutPositionAnnotation = new ArrayList<>();

        for (final String fieldName : fieldsInString) {
            final Index indexAnnotation = (Index) Reflection.getAnnotationFromField(this.clazz, Index.class, fieldName);
            final Label labelAnnotation = (Label) Reflection.getAnnotationFromField(this.clazz, Label.class, fieldName);
            final Field field = new Field();
            field.setNativeName(fieldName);
            if (labelAnnotation != null) {
                field.setLabel(labelAnnotation.value());
            }
            if (indexAnnotation != null) {
                // Index cannot be less than 1
                if (indexAnnotation.value() < 1)
                    throw new IndexCannotBeLessThan1Exception();
                field.setIndex(indexAnnotation.value() - 1);
                fieldsWithPositionAnnotation[field.getIndex()] = field;
            } else {
                fieldsWithoutPositionAnnotation.add(field);
            }
        }

        for (int i = 0; i < fieldsWithPositionAnnotation.length; i++) {
            if (fieldsWithPositionAnnotation[i] == null) {
                fieldsWithPositionAnnotation[i] = fieldsWithoutPositionAnnotation.stream().findFirst().orElseThrow(RepeatedIndexException::new);
                fieldsWithPositionAnnotation[i].setIndex(i);
                fieldsWithoutPositionAnnotation.remove(0);
            }
        }

        return Arrays.stream(fieldsWithPositionAnnotation).sorted(Comparator.comparing(Field::getIndex)).collect(Collectors.toList());
    }

    /**
     * @param filters {@param java.util.Collection}
     * @return {@link List}
     */
    @Override
    public List<Field> extractFields(final Collection<String> filters) {
        return this.extractFields()
                .stream()
                .filter(field -> filters
                        .stream()
                        .anyMatch(filter -> filter.equals(field.getLabel())))
                .collect(Collectors.toList());
    }

//    /**
//     * @return List<Field>
//     */
//    @Override
//    public List<Field> getFields() {
//        final Set<Field> fields = new HashSet<>();
//
//        final List<String> attributes = extractLabels();
//
//        try {
//            for (final String attribute : attributes) {
//                final io.github.emanuelvictor.csv.aspect.Field fieldAnnotation = clazz.getDeclaredField(attribute).getAnnotation(io.github.emanuelvictor.csv.aspect.Field.class);
//
//                final Field field = new Field(fieldAnnotation.label(), fieldAnnotation.index());
//                fields.add(field);
//            }
//        } catch (final NoSuchFieldException e) {
//            LOGGER.error(e.getMessage()); // TODO make fail test
//        }
//
//        return fields.stream().sorted(Comparator.comparing(Field::getIndex)).collect(Collectors.toList());
//    }

//    /**
//     * @return Set<Integer>
//     */
//    public List<Integer> extractIndexes(final Collection<String> filters) {
//
////        final Set<Field> fields = new HashSet<>();
////
////        final Set<String> attributes = Reflection.getAttributesFromClass(clazz);
////
////        try {
////            for (final String attribute : attributes) {
////                final io.github.emanuelvictor.csv.aspect.Field fieldAnnotation = clazz.getDeclaredField(attribute).getAnnotation(io.github.emanuelvictor.csv.aspect.Field.class);
////
////                final Field field = new Field(fieldAnnotation.label(), fieldAnnotation.index());
////                if (filters.stream().anyMatch(s -> s.equalsIgnoreCase(field.getLabel()))) //TODO make if test
////                    fields.add(field);
////            }
////        } catch (final NoSuchFieldException e) {
////            LOGGER.error(e.getMessage()); // TODO make fail test
////        }
////
////        return fields.stream().sorted(Comparator.comparing(Field::getIndex)).map(Field::getIndex).collect(Collectors.toList());
//        return null;
//    }

    /**
     * @param object
     * @param fieldsToExport
     * @return
     */
//    @Override
    public List<Object> extractValues(final Object object, final Collection<Field> filters) {

        for (final Field field : filters) {
            field.setValue(Reflection.getValueFromField(object, field.getNativeName()));
        }

        return filters.stream().map(Field::getValue).collect(Collectors.toList());
    }

    public InputStream convertToCSV(final List<T> list, final List<String> fieldsToExport) {
        return this.convertToCSV(list.stream(), fieldsToExport);
    }

    public InputStream convertToCSV(final List<T> list) {
        return this.convertToCSV(list.stream(), extractFields().stream().map(Field::getLabel).collect(Collectors.toList()));
    }

    public InputStream convertToCSV(final Stream<T> stream) {
        return this.convertToCSV(stream, extractFields().stream().map(Field::getLabel).collect(Collectors.toList()));
    }

    /**
     * @param stream         Stream
     * @param fieldsToExport List
     * @return InputStream
     */
    public InputStream convertToCSV(final Stream<T> stream, final List<String> fieldsToExport) {
        return convertToCSV(stream, fieldsToExport, integer -> {

        }, integer -> {

        });
    }

    /**
     * @param stream            Stream
     * @param lineConsumer      Consumer
     * @param lineErrorConsumer Consumer
     * @return InputStream
     */
    @Override
    public InputStream convertToCSV(final Stream<T> stream, final List<String> fieldsToExport /*TODO OVERLOAD*/, final Consumer<Integer> lineConsumer /*TODO OVERLOAD*/, final Consumer<Integer> lineErrorConsumer /*TODO OVERLOAD*/) {

        // This variable cannot be initialized with final modifier. Because we need to fix the security BUG "Unreleased Resource: Streams" found by fortify.
        // Read more in https://wiki.sei.cmu.edu/confluence/display/java/FIO04-J.+Release+resources+when+they+are+no+longer+needed , https://www.oracle.com/java/technologies/javase/seccodeguide.html#1
        File file = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        try {

            file = getFile(tempOutput);
            fileWriter = new FileWriter(file, true);
            bufferedWriter = getBufferedWriter(fileWriter);


            // Extract labels and writing header in file IN ORDER
            final List<Field> fields = extractFields(fieldsToExport);
            bufferedWriter.write(normalize(join(fields.stream().map(Field::getLabel).collect(Collectors.toList()), DEFAULT_SEPARATOR)) + "\n"); //TODO encapsulate in writeHeader method

            final BufferedWriter finalBufferedWriter = bufferedWriter;

            // Counter of lines
            final AtomicInteger currentLine = new AtomicInteger();
            stream.forEach(object -> {
                currentLine.getAndIncrement();
                try {

                    // *** Next line flux
                    // Extract values
                    final List<Object> values = extractValues(object, fields);
                    // Writing line in file
                    finalBufferedWriter.write(normalize(join(values, DEFAULT_SEPARATOR)) + "\n");  //TODO encapsulate in writeLine method

                    // Emmit next line event
                    lineConsumer.accept(currentLine.get());

                } catch (final Exception e) {
                    // *** Error in line flux
                    // Show de stack trace
                    LOGGER.error(e.getMessage());

                    lineErrorConsumer.accept(currentLine.get());

                }
            });

            // *** Done flux
            // After then running the stream.
            // Close writer
            flush(fileWriter, bufferedWriter);
            close(fileWriter, bufferedWriter); // Here fortify does not point out vulnerabilities.

            return Files.newInputStream(file.toPath());

        } catch (final Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            flush(fileWriter, bufferedWriter);
            close(fileWriter); // If you use the overloaded method 'close(final Writer... writers)', the fortify will point out a vulnerability.
            close(bufferedWriter); // If you use the overloaded method 'close(final Writer... writers)', the fortify will point out a vulnerability.
            // Delete de tmp file
//            delete(file);
        }

        throw new RuntimeException();
    }

    /**
     * Safe flush writers
     *
     * @param writers Writer...
     */
    public static void flush(final Writer... writers) {
        if (writers != null)
            Arrays.stream(writers).forEach(ReportProcessor::flush);
    }

    /**
     * Safe flush writer
     *
     * @param writer Writer
     */
    public static void flush(final Writer writer) {
        if (writer != null)
            try {
                writer.flush();
            } catch (final IOException e) {
                LOGGER.error(e.getMessage());
            }
    }

    /**
     * Safe close writers
     *
     * @param writers Writer[]
     */
    public static void close(final Writer... writers) {
        if (writers != null)
            Arrays.stream(writers).forEach(ReportProcessor::close);
    }

    /**
     * Safe close writers
     *
     * @param writer Writer
     */
    public static void close(final Writer writer) {
        if (writer != null)
            try {
                writer.close();
            } catch (final IOException e) {
                LOGGER.error(e.getMessage());
            }
    }

    /**
     * Safe delete files
     *
     * @param file File
     */
    public static void delete(final File file) {
        if (file != null)
            try {
                Files.deleteIfExists(file.toPath());
            } catch (final IOException e) {
                LOGGER.error(e.getMessage());
            }
    }

    /**
     * @param path String
     * @return File
     */
    public static File getFile(final String path) {
        try {
            final File file = new File(path);
            if (!file.exists())
                file.mkdirs();
            return new File(path + "/" + UUID.randomUUID() + ".csv");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * @param path String
     * @return File
     */
    public static File getFile(final String path, final String file) {
        final File filee = new File(path);
        if (!filee.exists())
            filee.mkdirs();
        return new File(path + "/" + file);
    }

    /**
     * @param fileWriter FileWriter
     * @return BufferedWriter
     */
    public static BufferedWriter getBufferedWriter(final FileWriter fileWriter) {
        return new BufferedWriter(fileWriter);
    }
}
