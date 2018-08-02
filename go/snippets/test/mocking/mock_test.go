package mocking

type MockDoStuffer struct {
	MockDoStuff func(input string) error
}

func (m *MockDoStuffer) DoStuff(input string) error {
	if m.MockDoStuff != nil {
		return m.MockDoStuff(input)
	}
	return nil
}
