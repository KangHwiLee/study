var header = $("meta[name='_csrf_header']").attr('content');
var token = $("meta[name='_csrf']").attr('content');
var ctx1 = document.getElementById('myChart1');
var ctx2 = document.getElementById('myChart2');
var label1 = [100]
var dataset1 = [100]
var label2 = [100]
var dataset2 = [100]
var label3 = [100]
var dataset3 = [100]
var label4 = [100]
var dataset4 = [100]
for(var i=0; i<100; i++){
    label1[i] = ''
    dataset1[i] = 0
    label2[i] = ''
    dataset2[i] = 0
    label3[i] = ''
    dataset3[i] = 0
    label4[i] = ''
    dataset4[i] = 0
}
                        var mychart1 = new Chart(ctx1, {
                            type: 'line',
                            data: {
                                labels: label1,
                                datasets: [{
                                    label: 'CPU 사용량',
                                    data: dataset1,
                                    fill: true,
                                    borderColor: 'rgb(78, 115, 223)',
                                    tension: 0.1,
                                    pointBorderWidth: 0
                                }]
                                },    
                            options: {
                                transitions : 0,
                                scales: {
                                    x: {
                                        display: false,     //세로선 없애기
                                        title: {
                                          display: true
                                        }
                                      },
                                    y: {
                                        display: true,
                                        suggestedMin: 0,
                                        suggestedMax: 100
                                      }
                                },
                                elements: {
                                    point:{
                                    radius: 0,
                                    }
                                    }
                            }          
                            })
                        var mychart2 = new Chart(ctx2, {
                            type: 'line',
                            data: {
                                labels: label2,
                                datasets: [{
                                    label: '메모리 사용량',
                                    data: dataset2,
                                    fill: true,
                                    borderColor: 'rgb(28, 200, 138)',
                                    tension: 0.1,
                                    pointBorderWidth: 0
                                }]
                                },    
                            options: {
                                transitions : 0,
                                scales: {
                                    x: {
                                        display: false,     //세로선 없애기
                                        title: {
                                          display: true
                                        }
                                      },
                                    y: {
                                        display: true,
                                        suggestedMin: 0,
                                        suggestedMax: 100
                                      }
                                },
                                elements: {
                                    point:{
                                    radius: 0,
                                    }
                                    }
                            }          
                            });    
                        mychart2.update();

mychart1.config.options.animation = false;
mychart2.config.options.animation = false;


$(function(){
    dataLoad();
    
})

let chartData;
function dataLoad(){
    return chartData = setInterval(function(){
        $.ajax({
            url : "/chart_data",
            type : "post",
            /*beforeSend: function(xhr){
                            xhr.setRequestHeader(header, token);
                        },*/
            success : function(data){
                addData(data.chart1, mychart1)
                addData(data.chart2, mychart2)
            }
        })
    }, 1000)
}

function addData(num, chart){

	var dataset = chart.config.data.datasets;
	for(var i=0; i<99; i++){
        dataset[0].data[i]=dataset[0].data[i+1]
    }
		dataset[0].data[99]=num		//데이터셋의 데이터 추가
    if(dataset[0].label.indexOf('CPU') > -1) dataset[0].label = 'CPU 사용량('+num+'%)';
    else if(dataset[0].label.indexOf('메모리') > -1) dataset[0].label = '메모리 사용량('+num+'%)';
	chart.update();
 }