package com.jovisco.pdf.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.jovisco.pdf", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTest {

    @ArchTest
    public static final ArchRule implement_general_coding_practices = CompositeArchRule.of(
            GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION);

    @ArchTest
    public static final ArchRule free_of_cycles =
            slices().matching("com.jovisco.pdf.(*)..").should().beFreeOfCycles();

    @ArchTest
    public static final ArchRule core_classes_not_depend_on_packages =
            noClasses().that().resideInAPackage("..core..")
                    .should().dependOnClassesThat().resideInAnyPackage("..base..", "..invoice..", "..shared..");

    @ArchTest
    public static final ArchRule base_classes_should_only_depend_on_core_packages =
            classes().that().resideInAPackage("..base..")
                    .should().dependOnClassesThat().resideInAnyPackage("..core..");

    @ArchTest
    public static final ArchRule base_classes_should_only_have_dependents_in_base_invoice_and_shared_packages =
            classes().that().resideInAPackage("..base..")
                    .should().onlyHaveDependentClassesThat().resideInAnyPackage("..base..", "..invoice..", "..shared");

    @ArchTest
    public static final ArchRule invoice_classes_should_only_depend_on_core_packages =
            classes().that().resideInAPackage("..invoice..")
                    .should().dependOnClassesThat().resideInAnyPackage("..core..");

    @ArchTest
    public static final ArchRule invoice_classes_should_only_have_dependents_in_invoice_and_shared_packages =
            classes().that().resideInAPackage("..invoice..")
                    .should().onlyHaveDependentClassesThat().resideInAnyPackage("..invoice..", "..shared");

    @ArchTest
    public static final ArchRule shared_classes_should_only_have_dependents_in_shared_packages =
            classes().that().resideInAPackage("..shared..")
                    .should().onlyHaveDependentClassesThat().resideInAnyPackage("..shared");
}
