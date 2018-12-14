var socket = new WebSocket("ws://127.0.0.1:8080");
socket.onopen = function() {
  socket.send("getId")
  socket.send("getFilters")

};

socket.onclose = function(event) {
  if (event.wasClean) {
    alert('Соединение закрыто чисто');
  } else {
    alert('Обрыв соединения');
  }
  alert('Код: ' + event.code + ' причина: ' + event.reason);
};

socket.onmessage = function(event) {
  switch (event.data.split(" ")[0]) {
    case "Filters":
      app.filters = event.data.split(" ").slice(1, 4)
      break;
    case "Id":
      app.id = event.data.split(" ")[1]
      break;
    case "ready":
      alert("Ready!")
      app.active = true
      app.getFile()
      break;
    case "counter":
      app.counter = event.data.split(" ")[1]
      break;
    default:
  }

};

socket.onerror = function(error) {
  alert("Ошибка " + error.message);
};

document.getElementById("file").addEventListener("triggered", function(e) {
  app.load(e)
});

var app = new Vue({
  el: '#app',
  data: {
    file: '',
    seen: true,
    id: "",
    filter: "Blur_3",
    filters: "",
    counter: 0,
    active: true
  },
  methods: {
    updateFile() {
      this.file = this.$refs.file.files[0];
    },
    load(evt) {
      if (!window.FileReader) return; // Browser is not compatible
      var reader = new FileReader();
      reader.onload = function(evt) {
        if (evt.target.readyState != 2) return;
        if (evt.target.error) {
          alert('Error while reading file');
          return;
        }
        filecontent = evt.target.result;
        console.log(filecontent);
        socket.send("getFilters")
      };
      reader.readAsText(evt.target.files[0]);
    },

    trigger(e) {
      var event = new CustomEvent("triggered");
      document.getElementById("file").dispatchEvent(event);
    },
    sendFile() {
      if (!this.active) {return}
      let formData = new FormData();
      formData.append('files', this.file);
      formData.append('id', this.id);
      formData.append('filter', this.filter)
      axios.post('http://127.0.0.1:8080/post',
          formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            }
          }
        ).then(function() {
          console.log('SUCCESS!!');
        })
        .catch(function() {
          console.log('FAILURE!!');
        }).then(function() {
          socket.send(app.filter)
        })
      this.active = false

    },

    getFile() {
      window.open('http://127.0.0.1:8080/' + this.id + ".jpg", '_blank');
    },

    stop() {
      this.active = true
    }

  }

})
