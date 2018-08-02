package negotiate

import "io"
import "github.com/unrolled/render"

func (n *Negotiator) Respond(w io.Writer, status int, v interface{}) {
	switch n.ContentType {
	case render.ContentJSON:
		n.Render.JSON(w, status, v)
	case render.ContentXML:
		n.Render.XML(w, status, v)
	default:
		n.Render.JSON(w, status, v)
	}
}
