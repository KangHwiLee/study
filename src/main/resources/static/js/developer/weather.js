var header = $("meta[name='_csrf_header']").attr('content');
var token = $("meta[name='_csrf']").attr('content');
$(function(){



    town_weather()

})

var t_date;

function town_weather(){
console.log(header)
    $.ajax({
        url : "/town_weather",
        type : "post",
        /*beforeSend: function(xhr){
                xhr.setRequestHeader(header, token);
            },*/
        success : function(data){
            t_date = data[1].data[0]

            data[1].data.forEach(function(a){
            var img_ck = a.pty == 0 ? 'sky' + a.sky +".png" : 'pty' + a.pty + ".png"
                var view="";
                var sky = a.sky == 1 ? '맑음' : a.sky == 3 ? ' 구름많음' : '흐림'
                                var pty = a.pty == 0 ? sky :
                                          a.pty == 1 ? '비' :
                                          a.pty == 2 ? '비/눈' :
                                          a.pty == 3 ? '눈' :
                                          a.pty == 5 ? '빗방울' :
                                          a.pty == 6 ? '빗방울눈날림' : '눈날림'

                var src = "/img/weather/"+img_ck
                view += "<div class='text_center'>"
                view += "<p>"+a.hour+"시</p>"
                view += "<img src='"+src+"'>"
                view += "<p>"+pty+"</p>"
                view += "<p>"+a.temp+"℃</p>"
                view += "<p>습도"+a.reh+"%</p>"
                view += "</div>"
                $(".town-weather").append(view)
            })
            now_weather()
        }
    })

}

function now_weather(){
var date = new Date()
    $.ajax({
        url : "/now_weather",
        type : "post",
        /*beforeSend: function(xhr){
                xhr.setRequestHeader(header, token);
            },*/
        success : function(data){
                $(".w-today h6").text(date.getHours() + "시")
                var view = "";
            data.forEach(function(a){
                if(a.category == 'PTY'){
                var sky = t_date.sky == 1 ? '맑음' : t_date.sky == 3 ? ' 구름많음' : '흐림'
                var pty = a.obsrValue == 0 ? sky :
                          a.obsrValue == 1 ? '비' :
                          a.obsrValue == 2 ? '비/눈' :
                          a.obsrValue == 3 ? '눈' :
                          a.obsrValue == 5 ? '빗방울' :
                          a.obsrValue == 6 ? '빗방울눈날림' : '눈날림'
                var img_ck = a.obsrValue == 0 ? 'sky' + t_date.sky: 'pty' + a.obsrValue
                $("#now-weather img").attr("src", "/img/weather/" + img_ck + ".png")
                $("#now-weather span").eq(0).text(pty)
                }else if(a.category == 'REH'){
                    view+="<p>습도 : "+a.obsrValue+"%</p>"
                }else if(a.category == 'RN1'){
                    if(a.obsrValue == 0){
                    view+="<p>강수없음</p>"
                    }else
                    view+="<p>강수량 : "+a.obsrValue+"mm</p>"
                }else if(a.category == 'T1H'){}
                else if(a.category == 'UUU'){}
                else if(a.category == 'VEC'){}
                else if(a.category == 'VVV'){}
                else{
                    view+="<p>풍속 : "+a.obsrValue+"m/s</p>"
                }
            })
            $(".w-detail h1").text(t_date.temp+"℃")
            $(".w-detail").append(view)
        }
    })
}