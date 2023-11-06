package org.example;

public class TextBlocks {

    /**
     * 1. Можно вставлять одинарные или двойные кавычки без проблем
     * 2. Отступы с левой стороны проставляются исходя из самой левой позиции в коде (ненужно прижимать к левому краю)
     * 3. Просто символ '\' означает не отрывать строку
     */
    static final String text = """
            hello, "I am multiline text block"
            you can write inside of me very
            long SQL block and prepared statements:
            '
                        CREATE OR REPLACE FUNCTION load_stored_books(in cell_id text) RETURNS text AS $$
                        DECLARE
                        stored_book_ids_string TEXT;
                        BEGIN
                        SELECT STRING_AGG(book_id::text, ', ')
                            INTO stored_book_ids_string
                        FROM warehouse_cell
                            WHERE warehouse_cell.cell_id = $1;
                            RETURN stored_book_ids_string::TEXT;
                        END $$ LANGUAGE plpgsql;;
            '
            """;

    public static void main(String[] args) {
        System.out.println(text);
    }
}
