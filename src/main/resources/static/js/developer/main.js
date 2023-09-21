$(document).on('click','#sidebarToggle',function(){
    if(sessionStorage.getItem("sideBar-view") == null || sessionStorage.getItem("sideBar-view") == 1){
    sessionStorage.setItem("sideBar-view", 0)
    }
    else
    sessionStorage.setItem("sideBar-view", 1)
})
$(document).ready(function(){
    if(sessionStorage.getItem("sideBar-view") == null || sessionStorage.getItem("sideBar-view") == 1){
    $("#accordionSidebar").removeClass('toggled')
    }else{
    $("#accordionSidebar").addClass('toggled')
    }
})

