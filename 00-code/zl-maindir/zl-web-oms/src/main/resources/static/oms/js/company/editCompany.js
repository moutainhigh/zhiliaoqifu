
$(document).ready(function () {
    editCompany.init();
})

var editCompany = {
    init: function () {
        editCompany.initEvent();
    },
    initEvent:function(){
        $('.btn-submit').on('click', editCompany.editCompanySubmit);
    },
    editCompanySubmit: function(){
        var companyId = $("#companyId").val();
        var name = $("#name").val();
        var lawCode = $("#lawCode").val();
        var address = $("#address").val();
        var contacts = $("#contacts").val();
        var phoneNo = $("#phoneNo").val();
        var remarks = $("#remarks").val();
        /*var transFlag = $("#transFlag").val();*/
        var isPlatform = $("#isPlatform").val();
        if(name ==''){
            Helper.alert("请输入企业名称");
            return false;
        }
        if(lawCode =='' || lawCode == null){
            Helper.alert("请输入统一社会信用代码");
            return false;
        } else {
            if (lawCode.length != 18) {
                Helper.alert("统一社会信用代码只能为18位");
                return false;
            }
        }
        if(address =='' || address == null){
            Helper.alert("请输入地址");
            return false;
        }
        if(contacts =='' || contacts == null){
            Helper.alert("请输入联系人");
            return false;
        }
        if(phoneNo =='' || phoneNo == null){
            Helper.alert("请输入联系电话");
            return false;
        } else {
            if (phoneNo.length < 11) {
                Helper.alert("手机号必须要是11位");
                return false;
            } else {
                var reg = /^1[3|4|5|7|8][0-9]{9}$/; //验证规则
                if (!(reg.test(phoneNo))) {
                    Helper.alert("请输入正确的联系电话");
                    return false;
                }
            }
        }
        /*if (transFlag == null || transFlag == "") {
            Helper.alert("请选择交易开关");
            return false;
        }*/
        if (isPlatform == null || isPlatform == "") {
            Helper.alert("请选择平台标识");
            return false;
        }
        $.ajax({
            url: Helper.getRootPath() + '/company/editCompany.do',
            type: 'post',
            dataType : "json",
            data: {
                "companyId" : companyId,
                "name" : name,
                "lawCode" : lawCode,
                "address" : address,
                "contacts" : contacts,
                "phoneNo" : phoneNo,
                /*"transFlag" : transFlag,*/
                "isPlatform" : isPlatform,
                "remarks" : remarks
            },
            traditional:true,
            success: function (data) {
                if(data.status) {
                    location = Helper.getRootPath() + '/company/listCompany.do?isPlatform='+isPlatform;
                }else {
                    Helper.alert(data.msg);
                    return false;
                }
            },
            error:function(){
                Helper.alert("系统故障，请稍后再试");
                return false;
            }
        });
    }
};

