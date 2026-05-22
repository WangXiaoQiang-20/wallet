# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build a fat JAR
mvn package

# Run (requires JVM arg from README.md on some JDK versions)
java --add-exports=java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -jar target/wallet-tool-jar-with-dependencies.jar
```

- Java 8 targeting, Maven project
- The fat JAR is produced by `maven-assembly-plugin` with main class `com.bsn.WalletTool`
- An external JAR (`lib/beautyeye_lnf.jar`) is bundled as a system-scope dependency

## Architecture

This is a **Java Swing desktop application** — a blockchain wallet utility tool built as a single JFrame with a `JTabbedPane` containing independent function panels.

### Entry point

`com.bsn.WalletTool` — Sets up the BeautyEye Look & Feel, creates the JFrame, instantiates each tab panel. Each tab panel is a static factory method (e.g., `WalletPanel.walletPanel()`) that returns a `JPanel` or `JSplitPane`.

### UI panels (`com.bsn.wallet`)

Each panel class follows the same pattern: a static factory method returning a Swing component, with inner `ActionListener` classes for button handlers. Panels are self-contained — they don't share state with each other.

| Panel | Purpose |
|-------|---------|
| `WalletPanel` | Generate BIP39 mnemonic wallets (BIP44 ETH derivation path `m/44'/60'/0'/0`) |
| `SwapPanel` | Convert public key / private key / mnemonic → ETH address |
| `KeystorePanel` | Generate Web3 keystore files (from generated mnemonic, provided private key, or provided mnemonic) |
| `KeystoreDecryptPanel` | Decrypt a keystore JSON (with password) to extract private key and address |
| `AesDecryptPanel` | AES-128-ECB encrypt/decrypt arbitrary text using a **hardcoded key** |
| `PemToHexPanel` | Convert PEM-format EC private key → hex private key + address (uses BouncyCastle) |
| `TransferPanel` | Send ETH transactions via Web3j HTTP provider (signs raw transactions, polls for receipt) |
| `TimePanel` | Displays current time (cosmetic) |
| `DeployContractPanel` | Duplicate of `KeystoreDecryptPanel` (not yet wired into the main tabbed pane) |

### Data model (`com.bsn.model`)

- **`OpbWalletFile`** — POJO mapping the Web3 keystore JSON structure (crypto, cipherparams, kdfparams, mac, version). Uses Jackson polymorphic deserialization for KDF params (`scrypt` vs `pbkdf2`/`aes-128-ctr`). Supports case-insensitive `crypto`/`Crypto` key in JSON.
- **`OpbWallet`** — Static methods to decrypt private keys from `OpbWalletFile` using either scrypt or PBKDF2 key derivation + AES-128-CTR decryption. `getPriKeyTwo()` tries hex first, falls back to UTF-8 string.

### Utilities (`com.bsn.utils`)

- `AesUtil` — AES-128-ECB encrypt/decrypt with a hardcoded key (used by the Aes panel)
- `FileUtil` — File read/write/delete helpers
- `JsonUtils` / `JsonFileUtil` — Jackson ObjectMapper wrapper; reads `OpbWalletFile` from JSON files
- `PemUtils` — Regex validation for PEM private key format
- `ResourcesUtil` — Resolves resource paths at runtime

### Key external libraries

- **web3j 5.0.0** — ETH wallet generation, transaction signing, HTTP JSON-RPC provider
- **bitcoinj-core 0.15.8** — BIP39/BIP32 HD key derivation
- **BouncyCastle 1.70** — SCrypt, PEM parsing, EC key operations
- **BeautyEye L&F** — Swing look-and-feel theme (external JAR in `lib/`)