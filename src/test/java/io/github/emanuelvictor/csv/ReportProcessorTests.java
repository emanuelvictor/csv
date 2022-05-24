package io.github.emanuelvictor.csv;

import io.github.emanuelvictor.csv.domain.Field;
import io.github.emanuelvictor.csv.example.domain.entity.Costumer;
import io.github.emanuelvictor.csv.example.domain.entity.Employee;
import io.github.emanuelvictor.csv.example.domain.entity.Address;
import io.github.emanuelvictor.csv.example.domain.entity.Student;
import io.github.emanuelvictor.csv.example.domain.service.*;
import io.github.emanuelvictor.csv.exception.IndexCannotBeLessThan1Exception;
import io.github.emanuelvictor.csv.exception.RepeatedIndexException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.join;

public class ReportProcessorTests {

    private static final IReportProcessor<Student> studentProcessorReport = new StudentReportProcessorImpl();


    private static final IReportProcessor<Address> addressIReportProcessor = new AddressReportProcessorImpl();

    private static final IReportProcessor<Employee> employeeProcessorReport = new EmployeeReportProcessorImpl();

    private static final IReportProcessor<Costumer> costumerProcessorReport = new CostumerReportProcessorImpl();

    /**
     *
     */
    @Test
    void extractAllFieldsFromEmployeeClassTest() {
        final List<String> fields = employeeProcessorReport.extractFields().stream().map(Field::getLabel).collect(Collectors.toList());;
        Assertions.assertEquals(15, fields.size());
        Assertions.assertEquals("id;name;surname;cpf;active;gender;registry;updatedOn;createdOn;crossLabel;crossLabel;fieldToNotIgnore;fieldToNotIgnoreWithLabel;repetedField;repetedField", join(fields, ';'));
    }

    /**
     *
     */
    @Test
    void extractIgnoringFieldsFieldsFromEmployeeClassTest() {
        final List<String> fields = employeeProcessorReport.extractFields().stream().map(Field::getLabel).collect(Collectors.toList());;
        Assertions.assertEquals(15, fields.size());
        Assertions.assertEquals("id;name;surname;cpf;active;gender;registry;updatedOn;createdOn;crossLabel;crossLabel;fieldToNotIgnore;fieldToNotIgnoreWithLabel;repetedField;repetedField", join(fields, ';'));
    }

    /**
     *
     */
    @Test
    void testExtractAllFieldsFromCostumerClassMustReturnDuplicatedPositionValueTest() {
        Assertions.assertThrows(RepeatedIndexException.class, costumerProcessorReport::extractFields);
    }

    /**
     *
     */
    @Test
    void indexCannotBeLessThan1ExceptionTest() {
        Assertions.assertThrows(IndexCannotBeLessThan1Exception.class, studentProcessorReport::extractFields);
    }

    /**
     *
     */
    @Test
    void extractLabelsWithIgnoringLabelsTest() {
        final List<String> labelsToIgnore = Arrays.asList("id","name","surname","cpf");
        final List<String> fields = employeeProcessorReport.extractFields(labelsToIgnore).stream().map(Field::getLabel).collect(Collectors.toList());
        Assertions.assertEquals(11, fields.size());
        Assertions.assertEquals("active;gender;registry;updatedOn;createdOn;crossLabel;crossLabel;fieldToNotIgnore;fieldToNotIgnoreWithLabel;repetedField;repetedField", join(fields, ';'));
    }

    /**
     *
     */
    @Test
    void reportTestWithFilters() {
        final List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Address address = new Address("name" + i, i, "street" + i, i);
            addresses.add(address);
        }

        addressIReportProcessor.convertToCSV(addresses.stream(), Arrays.asList("rua","nome"));
    }

    /**
     *
     */
    @Test
    void reportTestWithAllFields() {
        final List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Address address = new Address("name" + i, i, "street" + i, i);
            addresses.add(address);
        }

        addressIReportProcessor.convertToCSV(addresses);
    }

//    /**
//     *
//     */
//    @Test
//    @SneakyThrows
//    void extractIndexesToIgnoreFromCompleteReportMustPass() {
//
//        final List<Field> allFieldsFromReport = employeeProcessorReport.getFields();
//
//        final Set<Integer> indexesToIgnore = new HashSet<>();
//        // Removing 20 random labels from header
//        for (int i = 0; i < 20; i++) {
//            final int indexToIgnore = new Random().nextInt(allFieldsFromReport.size());
//
//            if (indexesToIgnore.stream().anyMatch(index -> index.equals(indexToIgnore)) || indexToIgnore == 0) {
//                i--;
//                continue;
//            }
//
//            indexesToIgnore.add(indexToIgnore);
//        }
//
//        final List<String> labelsToReport = allFieldsFromReport.stream().filter(field -> indexesToIgnore.stream().noneMatch(index -> index.equals(field.getIndex()))).map(Field::getLabel).collect(Collectors.toList());
//        final List<Integer> indexesToReport = allFieldsFromReport.stream().map(Field::getIndex).filter(index -> indexesToIgnore.stream().noneMatch(i -> i.equals(index))).collect(Collectors.toList());
//
//        final EReport eReport = new EReport();
//        eReport.setFields(labelsToReport);
//        eReport.setType(TypeReport.COMPLETE);
//
//        indexesToReport.forEach(indexToReport -> indexesToIgnore.forEach(indexToIgnore -> Assertions.assertNotEquals(indexesToIgnore, indexToIgnore)));
//
//        final List<Integer> indexesExtracted = new CompleteProcessorReportImpl().extractIndexes(eReport.getFields());
//        Assertions.assertEquals(indexesToReport, indexesExtracted);
//    }
}

