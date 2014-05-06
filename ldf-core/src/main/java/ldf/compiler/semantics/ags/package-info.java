/**
 * Abstract Grammar Specification.
 *
 * <p>This package includes a couple of classes specifically designed for
 * exposing the structure of a grammar in a simplified manner (as opposed
 * to the AST, which is rather verbose).
 * </p>
 * <p>A script (or a parser generator) will be given (read-only) access to
 * a grammar through a network of {@link ldf.compiler.semantics.ags.AgsNode}
 * objects, which is equivalent to the AST representation.
 * </p>
 *
 * @author Cristian Harja
 */
package ldf.compiler.semantics.ags;
