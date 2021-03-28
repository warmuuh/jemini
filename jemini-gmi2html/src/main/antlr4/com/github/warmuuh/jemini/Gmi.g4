grammar Gmi;


gmiFile : line*;
line : h1 CRLF
    | h2 CRLF
    | h3 CRLF
    | link CRLF
    | preFormatBlock
    | plainBlock
    ;

h1 : '# ' lineContent;
h2 : '## ' lineContent;
h3 : '### ' lineContent;
link: '=> ' url (WS lineContent)?;

preFormatBlock: (preFormat CRLF)+;
preFormat: '``` ' lineContent;

plainBlock: (lineContent CRLF)+;

url: WORD;
lineContent: (WORD WS?)+;


WORD: ~[ \t\r\n]+;
CRLF: [\r\n]+;
WS : [ \t]+;