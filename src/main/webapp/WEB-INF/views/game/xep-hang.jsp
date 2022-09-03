<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="cybersoft.javabackend.java18.gamedoanso.utils.UrlUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Ranking</title>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta name="viewport"
        content="width=device-width, initial-scale=1, shrink-to-fit=no">

  <!-- Bootstrap CSS -->
  <link rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
        integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
        crossorigin="anonymous">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light"
     style="background-color: #e3f2fd;">
  <a class="navbar-brand font-weight-bold" href="#">Trò Chơi Đoán Số</a>
  <div class="collapse navbar-collapse justify-content-center" id="navbarNav">
    <ul class="navbar-nav">
      <li class="nav-item active">
        <a class="nav-link font-weight-bold"
           href="<%=request.getContextPath() + UrlUtils.HOME%>">Home<span
            class="sr-only">(current)</span></a>
      </li>
      <li class="nav-item">
        <a class="nav-link font-weight-bold"
           href="<%=request.getContextPath() + UrlUtils.GAME%>">Game</a>
      </li>
      <li class="nav-item">
        <a class="nav-link font-weight-bold"
           href="<%=request.getContextPath() + UrlUtils.XEP_HANG%>">Ranking</a>
      </li>
    </ul>
  </div>
  <div class="nav-item dropdown">
    <a class="nav-link dropdown-toggle font-weight-bold" href="#" role="button"
       data-toggle="dropdown"
       aria-expanded="false">
      ${sessionScope.currentUser.username}
    </a>
    <ul class="dropdown-menu">
      <li><a class="dropdown-item"
             href="<%=request.getContextPath() + UrlUtils.DANG_XUAT%>">Đăng
        xuất</a>
      </li>
    </ul>
  </div>
</nav>
<div class="container mt-5">
  <div class="row">
    <h1 class="text-center">Raking</h1>
    <table class="table">
      <thead>
      <tr>
        <th scope="col">#</th>
        <th scope="col">Username</th>
        <th scope="col">Num Play</th>
        <th scope="col">Time PLay</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${ranks}" var="rank" varStatus="i">
        <tr>
          <th scope="row">${i.index + 1}</th>
          <td>${rank.username}</td>
          <td>${rank.numPlay}</td>
          <td>${rank.timePlay}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
</div>

<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
        crossorigin="anonymous"></script>
<script
    src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
    integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
    crossorigin="anonymous"></script>
<script
    src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
    integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
    crossorigin="anonymous"></script>
</body>
</html>