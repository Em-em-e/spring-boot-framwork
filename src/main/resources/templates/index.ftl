<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
</head>
<body>
<div>
    请输入手机号：<input id="mobile">
    <button onclick="submit()" type="button">确定</button>
</div>
<div>
    <textarea id="result" style="width: 80%;" rows="10">

    </textarea>
</div>

</body>
<script>
    function submit() {
        console.log(document.getElementById("mobile"));
        let mobile = $("#mobile").val();
        console.log(mobile);
        $.ajax({
            url: "http://localhost:8888/mobileInfo/"+mobile,
            type:'get',
            contentType: "application/json;charset=UTF-8",
            success: function (data) {
                console.log(data)
                if(data.errCode!=0){
                    alert(data.msg);
                }else {
                    $("#result").text(JSON.stringify(data.result));
                }
            }
        });
    }
</script>
</html>