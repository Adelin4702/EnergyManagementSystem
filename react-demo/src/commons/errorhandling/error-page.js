import React from 'react'
import styles from '../styles/project-style.module.css';

class ErrorPage extends React.Component {

    render() {
            return <h3 className={styles.errorTitle}>Page not found.</h3>;
    }
}

export default ErrorPage
