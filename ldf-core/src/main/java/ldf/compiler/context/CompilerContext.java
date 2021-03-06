package ldf.compiler.context;

import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.Scope;
import ldf.compiler.semantics.types.TypeEnv;

/**
 * @author Cristian Harja
 */
public interface CompilerContext extends Context {

    TypeEnv getTypeEnvironment();
    NsNode getGlobalNamespace();
    Scope getGlobalScope();

}
