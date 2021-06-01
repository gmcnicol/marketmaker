package io.nkdtrdr.mrktmkr.symbols;

public class SymbolsFacade {
    public boolean assetIsQuote(String asset, Symbol symbol) {
        return symbol.getQuoteSymbol().equals(asset);
    }
}
