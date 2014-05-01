/**
 * <p>These classes capture the (unresolved) types of expressions, as seen
 * by the parser, without looking up their actual types.
 * </p>
 * <p>The actual types will be resolved in another compiler stage, partly
 * because the compiler needs some extra information (such as a classpath)
 * which will be available long after the parser is finished.
 * </p>
 *
 * @author Cristian Harja
 */
package ldf.compiler.semantics.types;
