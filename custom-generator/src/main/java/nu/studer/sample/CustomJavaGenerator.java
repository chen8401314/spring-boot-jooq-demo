package nu.studer.sample;

import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.*;
import org.jooq.meta.jaxb.VisibilityModifier;
import org.jooq.tools.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
* @Title: jooq生成pojo拓展修改
* @Description:
* @Author: chenx
* @Date: 2025/2/27
*/
public class CustomJavaGenerator extends JavaGenerator {

    @Override
    protected void generatePojo(TableDefinition table, JavaWriter out) {
        this.generatePojo0(table, out);
    }

    private  void generatePojo0(Definition tableUdtOrEmbeddable, JavaWriter out) {
        String className = this.getStrategy().getJavaClassName(tableUdtOrEmbeddable, GeneratorStrategy.Mode.POJO);
        String interfaceName = this.generateInterfaces() ? out.ref(this.getStrategy().getFullJavaClassName(tableUdtOrEmbeddable, GeneratorStrategy.Mode.INTERFACE)) : "";
        String superName = out.ref(this.getStrategy().getJavaClassExtends(tableUdtOrEmbeddable, GeneratorStrategy.Mode.POJO));
        List<String> interfaces = out.ref(this.getStrategy().getJavaClassImplements(tableUdtOrEmbeddable, GeneratorStrategy.Mode.POJO));
        if (this.generateInterfaces()) {
            interfaces.add(interfaceName);
        }

        this.printPackage(out, tableUdtOrEmbeddable, GeneratorStrategy.Mode.POJO);
        if (tableUdtOrEmbeddable instanceof TableDefinition) {
            this.generatePojoClassJavadoc((TableDefinition) tableUdtOrEmbeddable, out);
        } else if (tableUdtOrEmbeddable instanceof EmbeddableDefinition) {
            this.generateEmbeddableClassJavadoc((EmbeddableDefinition) tableUdtOrEmbeddable, out);
        } else {
            this.generateUDTPojoClassJavadoc((UDTDefinition) tableUdtOrEmbeddable, out);
        }

        this.printClassAnnotations(out, tableUdtOrEmbeddable, GeneratorStrategy.Mode.POJO);
        if (tableUdtOrEmbeddable instanceof TableDefinition) {
            this.printTableJPAAnnotation(out, (TableDefinition) tableUdtOrEmbeddable);
        }

        out.println("%sclass %s[[before= extends ][%s]][[before= implements ][%s]] {", new Object[]{this.visibility(this.generateVisibilityModifier()), className, list(superName), interfaces});

        if (this.generateSerializablePojos() || this.generateSerializableInterfaces()) {
            out.printSerial();
        }

        out.println();
        if (!this.generatePojosAsJavaRecordClasses()) {
            for (TypedElementDefinition<?> column : this.getTypedElements(tableUdtOrEmbeddable)) {
                String javaMemberName = this.getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO);
                String commentName = this.escapeEntities(this.comment(column));
                commentName = commentName == null || "".equals(commentName) ? javaMemberName : commentName;
                out.println("/**");
                out.println(commentName);
                out.println(" */");
                out.println("private %s%s %s;", new Object[]{this.generateImmutablePojos() ? "final " : "", out.ref(this.getJavaType(column.getType(this.resolver(out, GeneratorStrategy.Mode.POJO)), out, GeneratorStrategy.Mode.POJO)), javaMemberName});
                out.println();
            }
        }

        if (!this.generateImmutablePojos() && !this.generatePojosAsJavaRecordClasses()) {
            this.generatePojoDefaultConstructor(tableUdtOrEmbeddable, out);
        }


        if (!this.generatePojosAsJavaRecordClasses()) {
            this.generatePojoCopyConstructor(tableUdtOrEmbeddable, out);
            this.generatePojoMultiConstructor(tableUdtOrEmbeddable, out);
        }

        List<? extends TypedElementDefinition<?>> elements = this.getTypedElements(tableUdtOrEmbeddable);

        for (int i = 0; i < elements.size(); ++i) {
            TypedElementDefinition<?> column = (TypedElementDefinition) elements.get(i);
            if (!this.generatePojosAsJavaRecordClasses() || this.generateInterfaces()) {
                if (tableUdtOrEmbeddable instanceof TableDefinition) {
                    this.generatePojoGetter(column, i, out);
                } else {
                    this.generateUDTPojoGetter(column, i, out);
                }
            }

            if (!this.generateImmutablePojos()) {
                if (tableUdtOrEmbeddable instanceof TableDefinition) {
                    this.generatePojoSetter(column, i, out);
                } else {
                    this.generateUDTPojoSetter(column, i, out);
                }
            }
        }


        if (tableUdtOrEmbeddable instanceof TableDefinition) {
            List<EmbeddableDefinition> embeddables = ((TableDefinition) tableUdtOrEmbeddable).getReferencedEmbeddables();
            for (int i = 0; i < embeddables.size(); ++i) {
                EmbeddableDefinition embeddable = (EmbeddableDefinition) embeddables.get(i);
                if (!this.generateImmutablePojos()) {
                    this.generateEmbeddablePojoSetter(embeddable, i, out);
                }

                this.generateEmbeddablePojoGetter(embeddable, i, out);
            }
        }

        if (this.generatePojosEqualsAndHashCode()) {
            this.generatePojoEqualsAndHashCode(tableUdtOrEmbeddable, out);
        }

        if (this.generatePojosToString()) {
            this.generatePojoToString(tableUdtOrEmbeddable, out);
        }

        if (this.generateInterfaces() && !this.generateImmutablePojos()) {
            this.printFromAndInto(out, tableUdtOrEmbeddable, GeneratorStrategy.Mode.POJO);
        }

        if (tableUdtOrEmbeddable instanceof TableDefinition) {
            this.generatePojoClassFooter((TableDefinition) tableUdtOrEmbeddable, out);
        } else if (tableUdtOrEmbeddable instanceof EmbeddableDefinition) {
            this.generateEmbeddableClassFooter((EmbeddableDefinition) tableUdtOrEmbeddable, out);
        } else {
            this.generateUDTPojoClassFooter((UDTDefinition) tableUdtOrEmbeddable, out);
        }

        out.println("}");
        this.closeJavaWriter(out);
    }

    private String visibility(VisibilityModifier modifier) {
        switch (modifier) {
            case NONE:
                return "";
            case PUBLIC:
                return "public ";
            case INTERNAL:
                return "public ";
            case PRIVATE:
                return "private ";
            case DEFAULT:
            default:
                return "public ";
        }
    }

    private String comment(Definition definition) {
        return (!(definition instanceof CatalogDefinition) || !this.generateCommentsOnCatalogs()) && (!(definition instanceof SchemaDefinition) || !this.generateCommentsOnSchemas()) && (!(definition instanceof TableDefinition) || !this.generateCommentsOnTables()) && (!(definition instanceof ColumnDefinition) || !this.generateCommentsOnColumns()) && (!(definition instanceof EmbeddableDefinition) || !this.generateCommentsOnEmbeddables()) && (!(definition instanceof UDTDefinition) || !this.generateCommentsOnUDTs()) && (!(definition instanceof AttributeDefinition) || !this.generateCommentsOnAttributes()) && (!(definition instanceof PackageDefinition) || !this.generateCommentsOnPackages()) && (!(definition instanceof RoutineDefinition) || !this.generateCommentsOnRoutines()) && (!(definition instanceof ParameterDefinition) || !this.generateCommentsOnParameters()) && (!(definition instanceof SequenceDefinition) || !this.generateCommentsOnSequences()) ? "" : StringUtils.defaultIfBlank(definition.getComment(), "");
    }


    private static <T> List<T> list(T... objects) {
        List<T> result = new ArrayList();
        if (objects != null) {
            for (T object : objects) {
                if (object != null && !"".equals(object)) {
                    result.add(object);
                }
            }
        }

        return result;
    }

    private void printFromAndInto(JavaWriter out, Definition tableOrUDT, GeneratorStrategy.Mode mode) {
        String qualified = out.ref(this.getStrategy().getFullJavaClassName(tableOrUDT, GeneratorStrategy.Mode.INTERFACE));
        out.header("FROM and INTO", new Object[0]);
        boolean override = this.generateInterfaces() && !this.generateImmutableInterfaces();
        out.overrideInheritIf(override);
        out.println("%svoid from(%s from) {", new Object[]{this.visibilityPublic(), qualified});

        for (TypedElementDefinition<?> column : this.getTypedElements(tableOrUDT)) {
            String setter = this.getStrategy().getJavaSetterName(column, GeneratorStrategy.Mode.INTERFACE);
            String getter = this.getStrategy().getJavaGetterName(column, GeneratorStrategy.Mode.INTERFACE);
            out.println("%s(from.%s());", new Object[]{setter, getter});
        }


        out.println("}");
        if (override) {
            out.overrideInherit();
            out.println("%s<E extends %s> E into(E into) {", new Object[]{this.visibilityPublic(), qualified});
            out.println("into.from(this);");
            out.println("return into;");
            out.println("}");
        }

    }

    private String visibilityPublic() {
        return "public ";
    }

    private List<? extends TypedElementDefinition<? extends Definition>> getTypedElements(Definition definition) {
        if (definition instanceof TableDefinition) {
            return ((TableDefinition) definition).getColumns();
        } else if (definition instanceof EmbeddableDefinition) {
            return ((EmbeddableDefinition) definition).getColumns();
        } else if (definition instanceof UDTDefinition) {
            return ((UDTDefinition) definition).getAttributes();
        } else if (definition instanceof RoutineDefinition) {
            return ((RoutineDefinition) definition).getAllParameters();
        } else {
            throw new IllegalArgumentException("Unsupported type : " + definition);
        }
    }

}
