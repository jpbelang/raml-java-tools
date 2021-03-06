package org.raml.builder;

import org.raml.v2.api.model.v10.methods.Method;
import org.raml.yagi.framework.nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class MethodBuilder extends KeyValueNodeBuilder<MethodBuilder> implements AnnotableBuilder<MethodBuilder>, ModelBuilder<Method> {

    private List<ResponseBuilder> responses = new ArrayList<>();
    private List<BodyBuilder> bodies = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();
    private List<ParameterBuilder> queryParameters = new ArrayList<>();
    private List<ParameterBuilder> headerParameters = new ArrayList<>();
    private String description;


    private MethodBuilder(String name) {
        super(name);
    }

    static public MethodBuilder method(String name) {

        return new MethodBuilder(name);
    }

    public MethodBuilder withResponses(ResponseBuilder... response) {

        responses.addAll(Arrays.asList(response));
        return this;
    }

    public MethodBuilder withBodies(BodyBuilder... builder) {

        this.bodies.addAll(Arrays.asList(builder));
        return this;
    }

    public MethodBuilder withQueryParameter(ParameterBuilder... builder) {

        this.queryParameters.addAll(Arrays.asList(builder));
        return this;
    }

    public MethodBuilder withHeaderParameters(ParameterBuilder... builder) {

        this.headerParameters.addAll(Arrays.asList(builder));
        return this;
    }

    @Override
    public MethodBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }

 //   @Override
 //   protected KeyValueNode createContainerNode() {
 //       return new MethodNode();
  //  }

    @Override
    public KeyValueNode buildNode() {
        KeyValueNode node =  super.buildNode();

        addProperty(node.getValue(), "description", description);

        if ( ! responses.isEmpty()) {
            ObjectNodeImpl responsesValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("responses"), responsesValueNode);

            for (ResponseBuilder response : responses) {

                responsesValueNode.addChild(response.buildNode());
            }

            node.getValue().addChild(kvn);
        }

        if ( ! queryParameters.isEmpty()) {
            ObjectNodeImpl responsesValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("queryParameters"), responsesValueNode);

            for (ParameterBuilder queryParameter : queryParameters) {

                responsesValueNode.addChild(queryParameter.buildNode());
            }

            node.getValue().addChild(kvn);
        }

        if ( ! headerParameters.isEmpty()) {
            ObjectNodeImpl responsesValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("headers"), responsesValueNode);

            for (ParameterBuilder quertParameter : headerParameters) {

                responsesValueNode.addChild(quertParameter.buildNode());
            }

            node.getValue().addChild(kvn);
        }

        if ( ! annotations.isEmpty() ) {

            for (AnnotationBuilder annotation : annotations) {
                node.getValue().addChild(annotation.buildNode());
            }
        }

        if ( ! bodies.isEmpty()) {
            ObjectNodeImpl bodyValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl bkvn = new KeyValueNodeImpl(new StringNodeImpl("body"), bodyValueNode);
            node.getValue().addChild(bkvn);

            for (BodyBuilder body : bodies) {
                bodyValueNode.addChild(body.buildNode());
            }
        }

        return node;

    }

    @Override
    public Method buildModel() {

        Node node = buildNode();

        return Util.buildModel(binding, node, Method.class);
    }

    public MethodBuilder description(String description) {
        this.description = description;
        return this;
    }

    public static void main(String[] args) {
        Method m = MethodBuilder.method("foo").description("hello").buildModel();
        System.err.println(m.description().value());
    }
}
