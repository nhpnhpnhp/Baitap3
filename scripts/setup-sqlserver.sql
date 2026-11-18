-- Chạy bằng SSMS với tài khoản có quyền tạo bảng (vd: sa). Không xóa dữ liệu đã có.
USE master;
GO
IF DB_ID(N'Baitap12') IS NULL CREATE DATABASE Baitap12;
GO
USE Baitap12;
GO
SET QUOTED_IDENTIFIER ON;
GO
IF OBJECT_ID(N'dbo.categories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.categories (
        CategoryId INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        CategoryName NVARCHAR(255) NOT NULL,
        Images NVARCHAR(500) NULL,
        status INT NOT NULL DEFAULT 1
    );
END;
GO
IF OBJECT_ID(N'dbo.Videos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Videos (
        VideoId VARCHAR(255) NOT NULL PRIMARY KEY,
        Active INT NOT NULL DEFAULT 1,
        Description NVARCHAR(500) NULL,
        Poster NVARCHAR(500) NULL,
        Title NVARCHAR(500) NULL,
        Views INT NOT NULL DEFAULT 0,
        CategoryId INT NULL,
        CONSTRAINT FK_Videos_Categories FOREIGN KEY (CategoryId) REFERENCES dbo.categories(CategoryId)
    );
END;
GO
IF OBJECT_ID(N'dbo.[User]', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.[User] (
        id INT IDENTITY(1,1) PRIMARY KEY,
        email NVARCHAR(255) NOT NULL UNIQUE,
        username NVARCHAR(100) NOT NULL UNIQUE,
        fullname NVARCHAR(255) NOT NULL,
        password NVARCHAR(255) NOT NULL,
        avatar NVARCHAR(255) NULL,
        roleid INT NOT NULL,
        phone NVARCHAR(30) NOT NULL,
        createddate DATE NOT NULL
    );
END;
GO
IF COL_LENGTH(N'dbo.[User]', N'active') IS NULL
    ALTER TABLE dbo.[User] ADD active BIT NOT NULL CONSTRAINT DF_User_Active DEFAULT 0;
IF COL_LENGTH(N'dbo.[User]', N'otp_code') IS NULL
    ALTER TABLE dbo.[User] ADD otp_code VARCHAR(10) NULL;
IF COL_LENGTH(N'dbo.[User]', N'otp_expiry') IS NULL
    ALTER TABLE dbo.[User] ADD otp_expiry DATETIME2 NULL;
IF COL_LENGTH(N'dbo.[User]', N'otp_type') IS NULL
    ALTER TABLE dbo.[User] ADD otp_type VARCHAR(30) NULL;
GO
IF COL_LENGTH(N'dbo.Videos', N'VideoPath') IS NULL
    ALTER TABLE dbo.Videos ADD VideoPath NVARCHAR(500) NULL;
IF COL_LENGTH(N'dbo.Videos', N'CreatedDate') IS NULL
    ALTER TABLE dbo.Videos ADD CreatedDate DATETIME2 NULL;
IF COL_LENGTH(N'dbo.Videos', N'UserId') IS NULL
BEGIN
    ALTER TABLE dbo.Videos ADD UserId INT NULL;
    ALTER TABLE dbo.Videos ADD CONSTRAINT FK_Videos_User FOREIGN KEY (UserId) REFERENCES dbo.[User](id);
END;
GO
IF EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'baitap12_app')
BEGIN
    GRANT SELECT, INSERT, UPDATE, DELETE ON dbo.categories TO baitap12_app;
    GRANT SELECT, INSERT, UPDATE, DELETE ON dbo.Videos TO baitap12_app;
    GRANT SELECT, INSERT, UPDATE, DELETE ON dbo.[User] TO baitap12_app;
END;
GO
SELECT CategoryId, CategoryName, Images, status FROM dbo.categories;
