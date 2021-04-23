grammar Gmi;


gmiFile : CRLF* (WS* line)*;
line : preFormatBlock
    | h1 CRLF
    | h2 CRLF
    | h3 CRLF
    | link CRLF
    | plainBlock
    | listBlock
    ;

h1 : '# ' lineContent;
h2 : '## ' lineContent;
h3 : '### ' lineContent;
link: '=> ' url (WS lineContent)?;

preFormatBlock: '```' CRLF? (lineContent CRLF)+ '```'  CRLF;

plainBlock: (lineContent CRLF)+;

listBlock: (listItem CRLF)+;
listItem: '* ' lineContent;

url: WORD;
lineContent: (WORD WS?)+;


WORD: ~[ \t\r\n]+;
CRLF: [\r\n]+;
WS : [ \t]+;