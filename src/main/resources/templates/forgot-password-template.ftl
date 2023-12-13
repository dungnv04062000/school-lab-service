<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="vi" lang="vi">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style>
        html {
            margin: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen",
            "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
            sans-serif;
        }
        body {
            height: 100vh;
            background-color: white;
        }
        .title {
            font-size: 20px;
        }
        .title span,
        .thanks {
            font-style: italic;
            font-size: 23px;
        }
        .link {
            display: inline-block;
            text-decoration: none;
            background-color: rgb(68, 152, 221);
            color: white;
            margin: 20px 0;
            padding: 10px 30px;
            border-radius: 5px;
            font-weight: 550;
            font-size: 23px;
            cursor: pointer;
            transition: all 0.2s linear;
        }
        .link:hover {
            background-color: rgb(34, 122, 194);
        }
        .detail {
            font-size: 27px;
            font-style: italic;
        }
        .contact span {
            font-style: italic;
        }
    </style>
</head>
<body>
<img src="https://schoollab-bucket.s3.ap-southeast-1.amazonaws.com/logo/School_Lab_Logo.png" alt="logo" />
<h4 class="title">
    Hello,
    <span> ${name} </span>
</h4>
<div class="content">
    You have requested to reset your password,
<br/>
    Please click the link below to reset your password
</div>
<a class="link" href="http://ec2-54-179-147-207.ap-southeast-1.compute.amazonaws.com:3000/reset-password?userId=${userId}"
>Reset Password</a
>

<br /><br />
<div class="thanks">Thanks, <br />Best Regards!</div>
<br />
<hr />
<div class="detail">
    <div class="slogan">SchoolLab - Practice to go further</div>
</div>
<br />
<div class="contact">
    <strong> Address: </strong>
    <span> KM29 Khu CNC Hoa Lac, Thach Hoa, Thach That, Ha Noi </span>
    <br />
    <strong> Email: </strong>
    <span> hotro.schoollab@gmail.com </span>
    <br />
    <strong> Phone: </strong>
    <span> 0859094608 </span>
</div>
</body>
</html>