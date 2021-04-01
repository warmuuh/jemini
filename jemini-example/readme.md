# test server

manual connections:
```
echo -e "gemini://localhost/test\r\n" | ncat --ssl 127.0.0.1 1965
echo "gemini://localhost/test\r\n" | openssl s_client -connect localhost:1965
```