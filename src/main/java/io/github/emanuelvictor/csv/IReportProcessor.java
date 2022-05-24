package io.github.emanuelvictor.csv;

import io.github.emanuelvictor.csv.domain.Field;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;


public interface IReportProcessor<T> {

    InputStream convertToCSV(final Stream<T> stream, final List<String> fieldsToExport /*TODO OVERLOAD*/, final Consumer<Integer> nextLineConsumer, final Consumer<Integer> lineErrorConsumer);

//    List<Object> extractValues(final Object object, final Collection<String> filters);

    List<Field> extractFields();

    List<Field> extractFields(final Collection<String> labelsToFilter);

    InputStream convertToCSV(final List<T> list);

    InputStream convertToCSV(final Stream<T> stream);

    InputStream convertToCSV(final List<T> list, final List<String> fieldsToExport);

    InputStream convertToCSV(final Stream<T> stream, final List<String> fieldsToExport);

//    List<Integer> extractIndexes(final Collection<String> fields);

}

