base-n-codec-java
# BaseN Encoder/Decoder (Java Version)

This little utility library implements an encoding an decoding algorithm to encode a sequence of bytes
into a sequence of (preferably printable) characters, and decode it back from this representation into
a byte sequence.

It is targeted at relatively short byte sequences (up to ~1000 bytes), which it treats as a single very long binary encoded integer number, and converts this number into the desired baseN encoding by subsequent division/remainder.
t is best suited e.g. for UUIDs, GUIDs, Hash Values (MD5, SHA1, SHA2 etc.) and similar binary constructs, which can be converted to a suitable representation e.g. for inserting this into XML, a database, or a plain text document.

There may be more efficient approaches than this.


