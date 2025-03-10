import {
  Box,
  Button,
  Card,
  CardContent,
  Divider,
  TextField,
  Typography,
  Snackbar,
  Alert,
  InputAdornment,
  IconButton,
  Paper,
} from "@mui/material";

import GoogleIcon from "@mui/icons-material/Google";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import PhoneOutlinedIcon from "@mui/icons-material/PhoneOutlined";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import { useEffect, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { getToken, setToken } from "../services/localStorageService";
import { message } from "antd";

function Login() {
  const navigate = useNavigate();
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const accessToken = getToken();
    if (accessToken) {
      navigate("/");
    }
  }, [navigate]);

  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  };

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setSnackBarOpen(false);
  };

  const handleGoogleLogin = () => {
    alert(
      "Please refer to Oauth2 series for this implementation guidelines. https://www.youtube.com/playlist?list=PL2xsxmVse9IbweCh6QKqZhousfEWabSeq"
    );
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setLoading(true);

    fetch("http://localhost:8081/api/v1/auth/login", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        phoneNumber: phoneNumber,
        password: password,
      }),
    })
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        console.log("Response body:", data);
        if (!data.result.authenticated) {
          message.error("Tài khoản hoặc mật khẩu không chính xác!");
        }
        if (data.code !== 1000) {
          throw new Error(data.message);
        }
        message.success("Đăng nhập thành công");
        setToken(data.result?.token);
        navigate("/");

      })
      .catch((error) => {
        setSnackBarMessage("Tài khoản hoặc mật khẩu không chính xác!");
        setSnackBarOpen(true);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <>
      <Snackbar
        open={snackBarOpen}
        onClose={handleCloseSnackBar}
        autoHideDuration={6000}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleCloseSnackBar}
          severity="error"
          variant="filled"
          sx={{ width: "100%" }}
        >
          {snackBarMessage}
        </Alert>
      </Snackbar>

      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        height="100vh"
        sx={{
          background: "linear-gradient(135deg, #667eea 0%,rgb(90, 193, 190) 100%)",
          padding: 2
        }}
      >
        <Paper
          elevation={10}
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            p: 1,
            width: "600px"
          }}
        >
          <Box
            sx={{
              bgcolor: "primary.main",
              color: "white",
              width: "60px",
              height: "60px",
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              mb: 2,
              mt: 2
            }}
          >
            <LockOutlinedIcon fontSize="large" />
          </Box>

          <Card
            sx={{
              minWidth: { xs: 400, sm: 600 },
              maxWidth: 600,
              borderRadius: 2,
              boxShadow: "none"
            }}
          >
            <CardContent sx={{ p: 3 }}>
              <Typography
                variant="h5"
                component="h1"
                align="center"
                fontWeight="bold"
                gutterBottom
                color="primary"
              >
                Đăng nhập
              </Typography>

              <Typography
                variant="body2"
                color="text.secondary"
                align="center"
                sx={{ mb: 3 }}
              >
                Welcome to Chis Tech Meets You
              </Typography>

              <Box
                component="form"
                display="flex"
                flexDirection="column"
                width="100%"
                onSubmit={handleSubmit}
              >
                <TextField
                  label="Số điện thoại"
                  variant="outlined"
                  fullWidth
                  margin="normal"
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  required
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <PhoneOutlinedIcon color="primary" />
                      </InputAdornment>
                    ),
                  }}
                />

                <TextField
                  label="Mật khẩu"
                  type={showPassword ? "text" : "password"}
                  variant="outlined"
                  fullWidth
                  margin="normal"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <LockOutlinedIcon color="primary" />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          aria-label="toggle password visibility"
                          onClick={handleClickShowPassword}
                          onMouseDown={handleMouseDownPassword}
                          edge="end"
                        >
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />

                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "flex-end",
                    mb: 2,
                    mt: 1
                  }}
                >
                  <NavLink
                    to="/forgot-password"
                    style={{
                      textDecoration: "none",
                      color: "primary.main"
                    }}
                  >
                    <Typography variant="body2" color="primary">
                      Quên mật khẩu?
                    </Typography>
                  </NavLink>
                </Box>

                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  size="large"
                  fullWidth
                  sx={{
                    mt: 1,
                    mb: 2,
                    py: 1.5,
                    textTransform: "none",
                    fontWeight: "bold",
                    fontSize: "1rem",
                    borderRadius: 2
                  }}
                  disabled={loading}
                >
                  {loading ? "Đang xử lý..." : "Đăng nhập"}
                </Button>
              </Box>

              <Divider sx={{ my: 3 }}>
                <Typography variant="body2" color="text.secondary">
                  Hoặc
                </Typography>
              </Divider>

              <Box display="flex" flexDirection="column" width="100%" gap={2}>
                <Button
                  type="button"
                  variant="outlined"
                  color="primary"
                  size="large"
                  onClick={handleGoogleLogin}
                  fullWidth
                  sx={{
                    gap: "10px",
                    textTransform: "none",
                    py: 1.2,
                    borderRadius: 2
                  }}
                  startIcon={<GoogleIcon />}
                >
                  Tiếp tục với Google
                </Button>

                <Box sx={{ mt: 2, textAlign: "center" }}>
                  <Typography variant="body2" color="text.secondary" display="inline">
                    Chưa có tài khoản?
                  </Typography>{" "}
                  <NavLink
                    to="/register"
                    style={{
                      textDecoration: "none",
                      fontWeight: "bold"
                    }}
                  >
                    <Typography variant="body2" color="primary" display="inline">
                      Đăng ký ngay
                    </Typography>
                  </NavLink>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Paper>
      </Box>
    </>
  );
}

export default Login;