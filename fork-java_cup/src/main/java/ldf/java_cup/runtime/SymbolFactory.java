package ldf.java_cup.runtime;

/**
 * Creates the Symbols interface, which CUP uses as default
 *
 * @version last updated 27-03-2006
 * @author Michael Petter
 */

/* *************************************************
  Interface SymbolFactory

  interface for creating new symbols
  You can also use this interface for your own callback hooks
  Declare Your own factory methods for creation of Objects in Your scanner!
 ***************************************************/
@SuppressWarnings("unused")
public interface SymbolFactory {
    /**
     * Construction with left/right propagation switched on
     */
    Symbol newSymbol(
            String name, int id,
            Symbol left, Symbol right,
            Object value
    );

    Symbol newSymbol(
            String name, int id,
            Symbol left, Symbol right
    );

    /**
     * Construction with left/right propagation switched off
     */
    Symbol newSymbol(String name, int id, Object value);
    Symbol newSymbol(String name, int id);

    /**
     * Construction of start symbol
     */
    Symbol startSymbol(String name, int id, int state);
}
