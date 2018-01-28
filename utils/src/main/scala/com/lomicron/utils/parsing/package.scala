package com.lomicron.utils

/**
 * Contains classes for parsing Clausewitz configuration files.
 * <p>
 * Files are normally parsed to low-level JSON objects. Serialization
 * to classes is performed via JSON.
 * <p>
 * {{{
 * val txt = getFileContents
 * val tokens = Tokenizer.tokenize(txt)
 * val (obj, errors) = JsonParser.parse(tokens)
 * }}}
 *
 */
package parsing