package main

import (
	"flag"
	"fmt"
	"os"
)

const version = "1.0.0"
const usage = `Usage:

%s [command]

Commands:
    Greet
    Version
`

const greetUsage = `Usage:

%s greet name [flag]

Positional Arguments:
    name
        the name to greet

Flags:
`

type MenuConf struct {
	Goodbye bool
}

func (m *MenuConf) SetupMenu() *flag.FlagSet {
	menu := flag.NewFlagSet("menu", flag.ExitOnError)
	menu.Usage = func() {
		fmt.Printf(usage, os.Args[0])
		menu.PrintDefaults()
	}
	return menu
}

func (m *MenuConf) GetSubMenu() *flag.FlagSet {
	submenu := flag.NewFlagSet("submenu", flag.ExitOnError)
	submenu.BoolVar(&m.Goodbye, "goodbye", false, "Say goodbye instead of hello")

	submenu.Usage = func() {
		fmt.Printf(greetUsage, os.Args[0])
		submenu.PrintDefaults()
	}
	return submenu
}

func (m *MenuConf) Greet(name string) {
	if m.Goodbye {
		fmt.Println("Goodbye " + name + "!")
	} else {
		fmt.Println("Hello " + name + "!")
	}
}

func (m *MenuConf) Version() {
	fmt.Println("Version: " + version)
}
