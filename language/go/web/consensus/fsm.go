package consensus

import (
	"io"

	"github.com/hashicorp/raft"
)

type FSM struct {
	state state
}

func NewFSM() *FSM {
	return &FSM{state: first}
}

func (f *FSM) Apply(r *raft.Log) interface{} {
	f.state.Transition(state(r.Data))
	return string(f.state)
}

func (f *FSM) Snapshot() (raft.FSMSnapshot, error) {
	return nil, nil
}

func (f *FSM) Restore(io.ReadCloser) error {
	return nil
}
