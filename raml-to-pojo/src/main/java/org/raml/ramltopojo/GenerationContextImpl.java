package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created. There, you have it.
 */
public class GenerationContextImpl implements GenerationContext {

    private final Api api;
    private final TypeFetcher typeFetcher;
    private final ConcurrentHashMap<String, CreationResult> knownTypes = new ConcurrentHashMap<>();
    private final String defaultPackage;

    public GenerationContextImpl(Api api) {
        this(api, TypeFetchers.NULL_FETCHER, "");
    }

    public GenerationContextImpl(Api api, TypeFetcher typeFetcher, String defaultPackage) {
        this.api = api;
        this.typeFetcher = typeFetcher;
        this.defaultPackage = defaultPackage;
    }

    @Override
    public CreationResult findCreatedType(String typeName, TypeDeclaration ramlType) {

        if ( knownTypes.containsKey(typeName) ) {

            return knownTypes.get(typeName);
        } else {

            TypeDeclaration typeDeclaration = typeFetcher.fetchType(api, typeName);
            CreationResult result =  TypeDeclarationType.typeHandler(typeDeclaration).create(this);
            knownTypes.put(typeName, result);
            return result;
        }
    }

    @Override
    public String defaultPackage() {
        return defaultPackage;
    }

    @Override
    public void createTypes(String rootDirectory) throws IOException {

        for (CreationResult creationResult : knownTypes.values()) {
            creationResult.createType(rootDirectory);
        }
    }

    @Override
    public Api api() {
        return api;
    }
}
