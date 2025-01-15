// httpCode e page são preenchidos por Programa Externo.

StringBuilder msgOk = new StringBuilder()
StringBuilder msgErro = new StringBuilder()

Boolean checkHttpCode = (httpCode / 100) == 2

if (checkHttpCode)
    msgOk.append("HTTP Code: ").append(httpCode).append(" - OK\n")
else
    msgErro.append("HTTP Code: ").append(httpCode).append(" - Erro\n")

Boolean checkPage = page.length() > 100

if (checkPage)
    msgOk.append("Page size: ").append(page.length()).append(" - OK\n")
else
    msgErro.append("Page size: ").append(page.length()).append(" - Erro\n")

return [msgOk  : msgOk.toString(),
        msgErro: msgErro.toString(),
        result : checkHttpCode && checkPage]
