package org.raml.ramltopojo.union;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.ClassName;
import org.junit.Test;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.GenerationContextImpl;
import org.raml.ramltopojo.RamlLoader;
import org.raml.ramltopojo.TypeFetchers;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldName;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldType;
import static org.raml.testutils.matchers.MethodSpecMatchers.*;
import static org.raml.testutils.matchers.ParameterSpecMatchers.type;
import static org.raml.testutils.matchers.TypeNameMatcher.typeName;
import static org.raml.testutils.matchers.TypeSpecMatchers.*;

/**
 * Created. There, you have it.
 */
public class UnionTypeHandlerTest {
    @Test
    public void create() throws Exception {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-type.raml"));
        UnionTypeHandler handler = new UnionTypeHandler(findTypes("foo", api.types()));

        CreationResult r = handler.create(new GenerationContextImpl(api, TypeFetchers.fromTypes(), "bar.pack"));


        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(contains(
                        allOf(methodName(equalTo("getFirst")), returnType(equalTo(ClassName.get("", "First")))),
                        allOf(methodName(equalTo("isFirst")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getSecond")), returnType(equalTo(ClassName.get("", "Second")))),
                        allOf(methodName(equalTo("isSecond")), returnType(equalTo(ClassName.get(Boolean.class).unbox())))
                ))
        )));

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(contains(
                        allOf(fieldName(equalTo("anyType")), fieldType(equalTo(ClassName.get(Object.class))))
                )),
                methods(contains(
                        allOf(methodName(equalTo("<init>"))),
                        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get("", "First")))))),
                        allOf(methodName(equalTo("getFirst")), returnType(equalTo(ClassName.get("", "First"))), codeContent(equalTo(
                                "if ( !(anyType instanceof  First)) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: First\");\nreturn (First) anyType;\n"))),
                        allOf(methodName(equalTo("isFirst")), returnType(equalTo(ClassName.get(Boolean.class).unbox())), codeContent(equalTo("return anyType instanceof First;\n"))),
                        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get("", "Second")))))),
                        allOf(methodName(equalTo("getSecond")), returnType(equalTo(ClassName.get("", "Second"))), codeContent(equalTo(
                                "if ( !(anyType instanceof  Second)) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: Second\");\nreturn (Second) anyType;\n"))),
                        allOf(methodName(equalTo("isSecond")), returnType(equalTo(ClassName.get(Boolean.class).unbox())), codeContent(equalTo("return anyType instanceof Second;\n")))
                )),
                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("bar.pack", "Foo"))))
                ))
        )));
    }


    private static UnionTypeDeclaration findTypes(final String name, List<TypeDeclaration> types) {
        return (UnionTypeDeclaration) FluentIterable.from(types).firstMatch(new Predicate<TypeDeclaration>() {
            @Override
            public boolean apply(@Nullable TypeDeclaration input) {
                return input.name().equals(name);
            }
        }).get();
    }

}