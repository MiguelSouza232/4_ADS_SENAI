import React from "react";

interface TemplateProps {
    children: React.ReactNode;
}

export const Template = (props: TemplateProps) => {
    return(
        <>
        //header

            {props.children}

        //footer
        </>
    )
}

const Header: React.FC = () => {
    return (
        <header className = "bg-red-600 text-white px-3">
            <div className="container mx auto flex items-center justify-between px-4">
                <h1 className="text-3xl font-bold">Imagem Peças</h1>
            </div>
        </header>
    )
}