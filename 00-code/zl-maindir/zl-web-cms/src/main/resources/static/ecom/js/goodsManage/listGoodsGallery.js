$(document).ready(function() {
	ListGoodsGallery.init();
})

var ListGoodsGallery = {
	init : function() {
        ListGoodsGallery.initEvent();
	},

	initEvent:function(){
		/*$('.btn-reset').on('click', ListGoodsGallery.searchReset);*/
		$('.btn-add').on('click', ListGoodsGallery.intoAddGoodsGallery);
		$('.btn-edit').on('click', ListGoodsGallery.intoEditGoodsGallery);
		$('.btn-delete').on('click', ListGoodsGallery.deleteGoodsGallery);
        $('.btn-view').on('click', ListGoodsGallery.intoViewGoodsGallery);
		$('.btn-close').on('click',ListGoodsGallery.searchReset);
        $('#originalFile').on('change', ListGoodsGallery.imageUpload);
        $('.btn-backGoodsInf').on('click',ListGoodsGallery.intoBackGoodsInf);
	},
	
	initTip: function (intoType) {
        var ttip_validator = $('.form_validation_tip').validate({
        	onkeyup: false,
            errorClass: 'error',
            validClass: 'valid',
            highlight: function (element) {
                $(element).closest('div').addClass("f_error");
            },
            unhighlight: function (element) {
                $(element).closest('div').removeClass("f_error");
            },
            rules:{
                thumbnail: { required: true},
                sort: { required: true}
            },
            messages: {
                thumbnail: { required: "请选择相册列表尺寸图"},
                sort: { required: "请输入序号"}
            },
            invalidHandler: function(form, validator) {
            },
            errorPlacement: function(error, element) {
                var elem = $(element);
                if (!error.is(':empty')) {
                    if ((elem.is(':checkbox')) || (elem.is(':radio'))) {
                        elem.filter(':not(.valid)').parent('label').parent('div').find('.error_placement').qtip({
                                overwrite: false,
                                content: error,
                                position: {
                                    my: 'left center',
                                    at: 'center right',
                                    viewport: $(window),
                                    adjust: {
                                        x: 6
                                    }
                                },
                                show: {
                                    event: false,
                                    ready: true
                                },
                                hide: false,
                                style: {
                                    classes: 'ui-tooltip-red ui-tooltip-rounded' // Make it red... the classic error colour!
                                }
                            }).qtip('option', 'content.text', error);
                    } else {
                        var xPoint = 5;
                        elem.filter(':not(.valid)').qtip({
                                overwrite: false,
                                content: error,
                                position: {
                                    my: 'left center',
                                    at: 'center right',
                                    viewport: $(window),
                                    adjust: { x: xPoint, y: 0 }
                                },
                                show: {
                                    event: false,
                                    ready: true
                                },
                                hide: false,
                                style: {
                                    classes: 'ui-tooltip-red ui-tooltip-rounded' // Make it red... the classic error colour!
                                }
                            })
                            // If we have a tooltip on this element already, just update its content
                            .qtip('option', 'content.text', error);

                    };
                }
                // If the error is empty, remove the qTip
                else {
                    if ((elem.is(':checkbox')) || (elem.is(':radio'))) {
                        elem.parent('label').parent('div').find('.error_placement').qtip('destroy');
                    } else {
                        elem.qtip('destroy');
                    }
                }
            },
            submitHandler: function (form) {
            	$(".btn-submit").attr('disabled',"true");
            	if(intoType == 1) {
                    ListGoodsGallery.addGoodsGallery();
            	} else if(intoType == 2) {
                    ListGoodsGallery.editGoodsGallery();
            	}
                return false;
            },
            success: $.noop 
        });
    },
    imageUpload : function() {
        var originalFile = $("#originalFile").val();
        $("#original").val(originalFile);
    },
	searchReset : function(){
	    var goodsId = $("#goodsId").val();
		Helper.post('/goodsManage/goodsInf/getGoodsGalleryList?goodsId=' + goodsId);
	},
	intoAddGoodsGallery : function(){
        ListGoodsGallery.loadModal(1, $(this).attr('imgId'));
        ListGoodsGallery.initTip(1);
	},
	intoEditGoodsGallery : function(){
        ListGoodsGallery.loadModal(2, $(this).attr('imgId'));
        ListGoodsGallery.initTip(2);
	},
    intoViewGoodsGallery : function(){
    ListGoodsGallery.loadModal(3, $(this).attr('imgId'));
    ListGoodsGallery.initTip(3);
    },
    intoBackGoodsInf : function () {
        Helper.post('/goodsManage/goodsInf/getGoodsInfList');
    },
	addGoodsGallery:function(){
        $.ajax({
            type: 'POST',
            url: Helper.getRootPath() + '/goodsManage/goodsInf/addGoodsGallery',
            data: new FormData($("#goodsGalleryInfo")[0]),
            processData: false,
            contentType: false,
            async: false,
            cache: false,
            dataType: 'json',
            success: function(data) {
                $(".btn-submit").removeAttr("disabled");
                if (data.code == '00') {
                    Helper.confirm_one('新增商品相册信息成功', function() {
                        ListGoodsGallery.searchReset();
                    });
                } else {
                    Helper.alert(data.msg);
                    return false;
                }
            },
            error : function() {
                Helper.alert("网络异常，请稍后再试");
            }
        });
    },
    editGoodsGallery:function(){
        $.ajax({
            type : 'POST',
            url : Helper.getRootPath() + '/goodsManage/goodsInf/editGoodsGallery',
            data: new FormData($("#goodsGalleryInfo")[0]),
            processData: false,
            contentType: false,
            async: false,
            cache: false,
            dataType: 'json',
            success : function(data) {
                $(".btn-submit").removeAttr("disabled");
                if (data.code == '00') {
                    Helper.confirm_one('编辑商品相册信息成功', function() {
                        ListGoodsGallery.searchReset();
                    });
                } else {
                    Helper.alert(data.msg);
                    return false;
                }
            },
            error : function() {
                Helper.alert("网络异常，请稍后再试");
            }
        });
    },
	deleteGoodsGallery : function(){
		var imgId = $(this).attr('imgId');
		if(imgId == null || imgId == '') {
			Helper.alert("系统故障，请稍后再试");
			return false;
		}
		Helper.confirm("你是否删除该记录？",function(){
			$.ajax({								  
				url : Helper.getRootPath() + '/goodsManage/goodsInf/deleteGoodsGallery',
				type : 'post',
				dataType : "json",
				data : {
					"imgId": imgId
				},
				success : function (data) {
					if(data.code == '00') {
						Helper.confirm_one('删除商品相册信息成功', function(){
                            ListGoodsGallery.searchReset();
	                	});
					} else {
						Helper.alert(data.msg);
						return false;
					}
				},
                error : function() {
                    Helper.alert("网络异常，请稍后再试");
                }
			});
		});
	},
	loadModal : function(type, imgId){
		$('#modal').modal({
			backdrop : "static"
		});
		if(type == 1){
			$('#modal_h').html("新增商品相册信息");
			return;
		}else if(type == 2){
			$('#modal_h').html("编辑商品相册信息");
		} else if(type == 3){
            $('#modal_h').html("商品相册信息详情");
            $("#originalFile").hide();
            $("#isDefaultList").attr("disabled","true");
            $("#sort").attr("readonly","readonly");
            $("#remarks").attr("disabled","true");
            $(".btn-submit").attr("disabled","true");
        }
		
		$.ajax({								  
            url: Helper.getRootPath() + '/goodsManage/goodsInf/getGoodsGallery',
            type: 'post',
            dataType : "json",
            data: {
                "imgId": imgId
            },
            success : function (data) {
            	$('#img_id').val(data.imgId);
            	$('#original').val(data.original);
            	$('#isDefaultList').val(data.isDefault);
            	$('#sort').val(data.sort);
                $('#remarks').val(data.remarks);
            },
            error : function() {
                Helper.alert("网络异常，请稍后再试");
            }
	    });
		
		$("#modal").on("hidden.bs.modal", function(e) {
            $("#originalFile").show();
			$("#isDefaultList").removeAttr('disabled');
			$("#sort").removeAttr('readonly');
            $("#remarks").removeAttr('readonly');
			$(".btn-submit").removeAttr('disabled');
		});
	}
}
