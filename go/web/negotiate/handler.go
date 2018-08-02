package negotiate

import (
	"encoding/xml"
	"net/http"
)

type Payload struct {
	XMLName xml.Name `xml:"payload" json:"-"`
	Status  string   `xml:"status" json:"status"`
}

func Handler(w http.ResponseWriter, r *http.Request) {
	n := GetNegotiator(r)
	n.Respond(w, http.StatusOK, &Payload{Status: "Successful!"})
}
