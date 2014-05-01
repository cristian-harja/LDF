package ldf.compiler;

import ldf.Context;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.types.TypeEnv;

/**
 * @author Cristian Harja
 */
public interface CompilerContext extends Context {

    TypeEnv getTypeEnvironment();
    NsNode getGlobalNamespace();

}
