package main

import (
	"bytes"
	"fmt"
	"os"

	"strings"

	"github.com/jprichardson/readline-go"
)

func main() {
	var buff bytes.Buffer
	readline.ReadLine(os.Stdin, func(line string) {
		if strings.HasPrefix(line, "==> ") {
			if buff.Len() > 0 {
				buff.WriteString("********************end********************\n")
				fmt.Println(buff.String())
				buff.Reset()
			}
			buff.WriteString("*******************start*********************\n")
		}
		buff.WriteString(line)
		buff.WriteString("\n")
	})
	if buff.Len() > 0 {
		buff.WriteString("********************end********************\n")
		fmt.Println(buff.String())
	}
}
