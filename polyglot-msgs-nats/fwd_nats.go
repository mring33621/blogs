package main

import (
	"bytes"
	"os"

	"strings"

	"github.com/jprichardson/readline-go"
	"github.com/nats-io/go-nats"
)

func sendMsg(nc *nats.Conn, subject string, msgBody string) {
	nc.Publish(subject, []byte(msgBody))
}

func main() {

	nc, _ := nats.Connect(nats.DefaultURL)

	var buff bytes.Buffer
	readline.ReadLine(os.Stdin, func(line string) {
		if strings.HasPrefix(line, "==> ") {
			if buff.Len() > 0 {
				buff.WriteString("********************end********************\n")
				msgBody := buff.String()
				buff.Reset()
				sendMsg(nc, "BigBrother", msgBody)
			}
			buff.WriteString("*******************start*********************\n")
		}
		buff.WriteString(line)
		buff.WriteString("\n")
	})
	if buff.Len() > 0 {
		buff.WriteString("********************end********************\n")
		sendMsg(nc, "BigBrother", buff.String())
	}

	nc.Close()
}
