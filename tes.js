// function wait(ms){
//    var start = new Date().getTime();
//    var end = start;
//    while(end < start + ms) {
//      end = new Date().getTime();
//   }
// }
// let z = 170
//
// //
// var a = []
// var at = []
// var start = new Date().getTime();
//
//   for (var i = 0; i < z; i++) {
//     let s = new WebSocket("ws://192.168.1.168:8080");
//     s.at = i
//     s.onopen = function() {
//     s.send("getId")
//     };
//
//     s.onclose = function(event) {
//       if (event.wasClean) {
//         alert('Соединение закрыто чисто');
//       } else {
//         alert('Обрыв соединения');
//       }
//       alert('Код: ' + event.code + ' причина: ' + event.reason);
//     };
//
//     s.onmessage = function(event) {
//       switch (event.data.split(" ")[0]) {
//         case "Filters":
//           app.filters = event.data.split(" ").slice(1, 4)
//           break;
//         case "Id":
//           app.id = event.data.split(" ")[1]
//           console.log(app.id)
//           a.push([s, app.id, new Date().getTime()])
//           break;
//         case "ready":
//           console.log(new Date().getTime() - start);
//           app.active = true
//           // app.getFile()
//           break;
//         case "counter":
//           app.counter = event.data.split(" ")[1]
//           break;
//         default:
//       }
//     };
//
//     s.onerror = function(error) {
//       alert("Ошибка " + error.message);
//     };
//   }
//
// function send(){
//     start = new Date().getTime();
//     for (var i = 0; i < z; i++) {
//       while (a.length != z){}
//       let formData = new FormData();
//       formData.append('filter', app.filter);
//       formData.append('files', app.file);
//       formData.append('id', a[i][1]);
//       at[i] = new Date().getTime();
//       axios.post('http://192.168.1.168:8080/post',
//           formData, {
//             headers: {
//               'Content-Type': 'multipart/form-data',
//             }
//           }
//         ).then(function() {
//           console.log('SUCCESS!!');
//         })
//         .catch(function() {
//           console.log('FAILURE!!');
//         })
//     }
//     console.log(new Date().getTime() - start)
//
// }
